
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Involves;
import acme.entities.task.Task;
import acme.realms.Technician;

@GuiService
public class TaskInvolvedInMaintenanceRecordCreateService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;

		int masterId;
		MaintenanceRecord maintenanceRecord;
		boolean status1 = true;
		if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
			status1 = false;
		if (super.getRequest().hasData("masterId", int.class)) {
			masterId = super.getRequest().getData("masterId", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
			status = maintenanceRecord != null && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()) && maintenanceRecord.isDraftMode();
			if (super.getRequest().hasData("task", Integer.class)) {
				Integer taskId = super.getRequest().getData("task", Integer.class);
				if (taskId == null)
					status = false;
				else if (taskId != 0) {
					Task checkedTask = this.repository.findTaskById(taskId);
					Involves i = this.repository.findInvolvedInTMR(masterId, taskId);
					status = status && checkedTask != null && i == null && !checkedTask.isDraftMode() && checkedTask.getTechnician().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
				}
			}
		}

		super.getResponse().setAuthorised(status && status1);
	}

	@Override
	public void load() {
		Involves involvedIn;
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);

		involvedIn = new Involves();
		involvedIn.setTask(null);
		involvedIn.setMaintenanceRecord(maintenanceRecord);

		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void bind(final Involves involvedIn) {
		int taskTicker;
		int masterId;
		Task task;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		taskTicker = super.getRequest().getData("task", int.class);
		task = this.repository.findTaskById(taskTicker);

		super.bindObject(involvedIn);
		involvedIn.setTask(task);
		involvedIn.setMaintenanceRecord(maintenanceRecord);
	}

	@Override
	public void validate(final Involves involvedIn) {
		boolean valid = involvedIn.getTask() != null;
		super.state(valid, "task", "acme.validation.form.error.invalidTask");
	}

	@Override
	public void perform(final Involves involvedIn) {
		this.repository.save(involvedIn);
	}

	@Override
	public void unbind(final Involves involvedIn) {

		Collection<Task> tasks;
		SelectChoices choices;
		Dataset dataset;
		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<Task> eliminateTasks = this.repository.findAllInvolvedInMaintenanceRecord(involvedIn.getMaintenanceRecord().getId());
		tasks = this.repository.findTasksDisponiblesByTechnicianId(technicianId);
		tasks.removeAll(eliminateTasks);

		choices = SelectChoices.from(tasks, "description", involvedIn.getTask());

		dataset = super.unbindObject(involvedIn);
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);

		super.getResponse().addData(dataset);
	}

}
