
package acme.features.administrator.involves;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Involves;
import acme.entities.task.TaskType;

@GuiService
public class AdministratorInvolvedInShowTaskService extends AbstractGuiService<Administrator, Involves> {

	@Autowired
	private AdministratorInvolvedInRepository repository;


	@Override
	public void authorise() {

		boolean status;
		boolean status1 = true;
		if (!super.getRequest().getMethod().equals("GET"))
			status1 = false;
		if (super.getRequest().hasData("id", int.class)) {
			int masterId = super.getRequest().getData("id", int.class);
			Involves i = this.repository.findInvolvedInById(masterId);
			if (i != null)
				status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && !i.getMaintenanceRecord().isDraftMode();
			else
				status = false;
		} else
			status = false;
		super.getResponse().setAuthorised(status && status1);
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
		dataset = super.unbindObject(involvedIn);

		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, involvedIn.getTask().getType());

		dataset.put("ticker", involvedIn.getTask().getTicker());
		dataset.put("description", involvedIn.getTask().getDescription());
		dataset.put("priority", involvedIn.getTask().getPriority());
		dataset.put("estimatedDuration", involvedIn.getTask().getEstimatedDuration());
		dataset.put("type", choices.getSelected().getKey());
		dataset.put("types", choices);
		dataset.put("technician", involvedIn.getTask().getTechnician().getLicenseNumber());
		dataset.put("masterId", involvedIn.getMaintenanceRecord().getId());
		super.getResponse().addData(dataset);
	}
}
