
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
public class TechnicianTaskCreateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
			status = false;
		if (super.getRequest().getMethod().equals("POST")) {
			int id = super.getRequest().getData("id", int.class);
			status = id == 0;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;
		Technician technician;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		task = new Task();
		task.setTechnician(technician);
		task.setDraftMode(true);
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
		dataset.put("tasks", choices);

		super.getResponse().addData(dataset);
	}
}
