
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
public class TaskInvolvedInMaintenanceRecordDeleteService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		int masterId;
		MaintenanceRecord maintenanceRecord;
		boolean status1 = true;
		try {
			String method = super.getRequest().getMethod();
			if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
				status1 = false;
			if (super.getRequest().getMethod().equals("POST")) {
				int id = super.getRequest().getData("id", int.class);
				status1 = id == 0;
			}
			if (super.getRequest().hasData("masterId", int.class)) {
				masterId = super.getRequest().getData("masterId", int.class);
				maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
				status = maintenanceRecord != null && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()) && maintenanceRecord.isDraftMode();

				if (super.getRequest().hasData("task", Integer.class)) {
					Integer taskId = super.getRequest().getData("task", Integer.class);
					if (taskId == null)
						status = false;
					if (taskId != 0) {
						Task checkedTask = this.repository.findTaskById(taskId);
						Involves i = this.repository.findInvolvedInTMR(masterId, taskId);
						status = status && checkedTask != null && i != null;
					}
				}
			}
		} catch (Throwable t) {
			status = false;
		}
		super.getResponse().setAuthorised(status && status1);
	}
	@Override
	public void load() {

		Involves involvedIn = new Involves();
		int masterId = super.getRequest().getData("masterId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		involvedIn.setMaintenanceRecord(maintenanceRecord);

		super.getBuffer().addData(involvedIn);
	}
	@Override
	public void bind(final Involves involvedIn) {

		super.bindObject(involvedIn, "task");

	}

	@Override
	public void validate(final Involves involvedIn) {
		boolean valid = involvedIn.getTask() != null;
		super.state(valid, "task", "acme.validation.form.error.invalidTask");
	}
	@Override
	public void perform(final Involves involvedIn) {
		Involves toDelete = this.repository.findInvolvedInByTaskIdAndMaintenanceRecordId(involvedIn.getTask().getId(), involvedIn.getMaintenanceRecord().getId());

		this.repository.delete(toDelete);
	}

	@Override
	public void unbind(final Involves involvedIn) {
		Collection<Task> tasks;
		SelectChoices choices;
		Dataset dataset;
		tasks = this.repository.findAllInvolvedInMaintenanceRecord(involvedIn.getMaintenanceRecord().getId());

		choices = SelectChoices.from(tasks, "description", involvedIn.getTask());

		dataset = super.unbindObject(involvedIn);
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);

		super.getResponse().addData(dataset);
	}

}
