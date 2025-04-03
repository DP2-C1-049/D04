
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
public class TaskInvolvedInMaintenanceRecordShowService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Involves involvedIn;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);
		status = involvedIn != null;

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		Involves involvedIn;
		int id;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);

		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void unbind(final Involves involvedIn) {
		Dataset dataset;
		SelectChoices choices;
		Collection<Task> tasks;
		MaintenanceRecord maintenanceRecord = involvedIn.getMaintenanceRecord();

		tasks = this.repository.findTasksDisponibles();
		choices = SelectChoices.from(tasks, "description", involvedIn.getTask());

		dataset = super.unbindObject(involvedIn);
		dataset.put("tasks", choices);
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("priority", involvedIn.getTask().getPriority());
		dataset.put("technician", involvedIn.getTask().getTechnician().getLicenseNumber());
		dataset.put("draftMode", maintenanceRecord.isDraftMode());
		super.getResponse().addData(dataset);
	}

}
