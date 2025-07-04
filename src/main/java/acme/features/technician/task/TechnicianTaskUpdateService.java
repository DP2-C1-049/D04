
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Task;
import acme.entities.task.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskUpdateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		try {
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
		} catch (Throwable t) {
			status = false;
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

		Task existTask = this.repository.findTaskByTicker(task.getTicker());
		boolean valid = existTask == null || existTask.getId() == task.getId();
		super.state(valid, "ticker", "acme.validation.form.error.duplicateTicker");
	}

	@Override
	public void perform(final Task task) {
		this.repository.save(task);
	}
	@Override
	public void unbind(final Task task) {

		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, task.getType());
		dataset = super.unbindObject(task, "ticker", "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("tasks", choices);
		super.getResponse().addData(dataset);
	}

}
