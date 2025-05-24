
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
import acme.entities.task.TaskType;
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

		if (involvedIn != null)
			status = super.getRequest().getPrincipal().getActiveRealm().getId() == involvedIn.getMaintenanceRecord().getTechnician().getId() && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		else
			status = false;
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

		SelectChoices types;
		types = SelectChoices.from(TaskType.class, involvedIn.getTask().getType());

		dataset = super.unbindObject(involvedIn);
		dataset.put("tickerMR", involvedIn.getMaintenanceRecord().getTicker());
		dataset.put("estimatedDuration", involvedIn.getTask().getEstimatedDuration());
		dataset.put("tasks", choices);
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("types", types);
		dataset.put("type", types.getSelected().getKey());
		dataset.put("ticker", involvedIn.getTask().getTicker());
		dataset.put("priority", involvedIn.getTask().getPriority());
		dataset.put("technician", involvedIn.getTask().getTechnician().getLicenseNumber());
		dataset.put("draftMode", maintenanceRecord.isDraftMode());
		dataset.put("masterId", involvedIn.getMaintenanceRecord().getId());
		super.getResponse().addData(dataset);
	}

}
