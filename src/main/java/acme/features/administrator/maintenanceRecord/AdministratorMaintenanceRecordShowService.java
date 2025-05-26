
package acme.features.administrator.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenaceRecordStatus;
import acme.entities.maintenanceRecord.MaintenanceRecord;

@GuiService
public class AdministratorMaintenanceRecordShowService extends AbstractGuiService<Administrator, MaintenanceRecord> {

	@Autowired
	private AdministratorMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		boolean status1 = true;
		if (super.getRequest().getMethod().equals("GET") && !super.getRequest().hasData("id", int.class))
			status1 = false;
		if (!super.getRequest().getMethod().equals("GET"))
			status1 = false;
		if (super.getRequest().hasData("id", int.class)) {
			int masterId = super.getRequest().getData("id", int.class);
			MaintenanceRecord mr = this.repository.findMaintenanceRecordById(masterId);
			if (mr != null)
				status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && !mr.isDraftMode();
			else
				status = false;
		}
		super.getResponse().setAuthorised(status && status1);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		SelectChoices choices;
		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		Dataset dataset;
		aircraftsCollection = this.repository.findAircrafts();
		aircrafts = SelectChoices.from(aircraftsCollection, "registrationNumber", maintenanceRecord.getAircraft());

		choices = SelectChoices.from(MaintenaceRecordStatus.class, maintenanceRecord.getStatus());

		dataset = super.unbindObject(maintenanceRecord, "ticker", "moment", "nextInspectionDueTime", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());
		super.getResponse().addData(dataset);

	}

}
