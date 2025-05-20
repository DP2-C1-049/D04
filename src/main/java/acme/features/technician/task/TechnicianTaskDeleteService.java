
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Involves;
import acme.entities.task.Task;
import acme.entities.task.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int taskId;
			Task task;
			Technician technician;

			taskId = super.getRequest().getData("id", int.class);
			task = this.repository.findTaskById(taskId);
			technician = task == null ? null : task.getTechnician();
			status = task != null && task.isDraftMode() && super.getRequest().getPrincipal().hasRealm(technician);
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);
	}
	@Override
	public void bind(final Task task) {

		super.bindObject(task, "ticker", "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		Collection<Involves> relationsInvolvedIn;
		relationsInvolvedIn = this.repository.findTaskInvolvedIn(task.getId());
		boolean valid = relationsInvolvedIn.isEmpty();
		super.state(valid, "*", "acme.validation.form.error.TaskInvolved");
	}
	@Override
	public void perform(final Task task) {
		this.repository.delete(task);
	}

	@Override
	public void unbind(final Task task) {

		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, task.getType());
		dataset = super.unbindObject(task, "ticker", "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("tasks", choices);
		super.getResponse().addData(dataset);
	}
}
