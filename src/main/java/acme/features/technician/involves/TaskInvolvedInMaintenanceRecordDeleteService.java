
package acme.features.technician.involves;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Involves;
import acme.entities.task.Task;
import acme.realms.Technician;

@GuiService
public class TaskInvolvedInMaintenanceRecordDeleteService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Involves involvedIn;

		id = super.getRequest().getData("id", int.class);
		involvedIn = this.repository.findInvolvedInById(id);
		status = involvedIn != null && super.getRequest().getPrincipal().hasRealm(involvedIn.getMaintenanceRecord().getTechnician());
		;

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
	public void bind(final Involves involvedIn) {
		int taskId;
		Task task;

		taskId = super.getRequest().getData("task", int.class);
		task = this.repository.findTaskById(taskId);

		super.bindObject(involvedIn);
		involvedIn.setTask(task);
	}

	@Override
	public void validate(final Involves involvedIn) {
		;
	}
	@Override
	public void perform(final Involves involvedIn) {
		this.repository.delete(involvedIn);
	}

	@Override
	public void unbind(final Involves involvedIn) {
		Dataset dataset;
		dataset = super.unbindObject(involvedIn);
		dataset.put("task", involvedIn.getTask().getDescription());

		super.getResponse().addData(dataset);
	}

}
