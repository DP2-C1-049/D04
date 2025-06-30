
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenaceRecordStatus;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		boolean status2 = true;
		try {
			status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
			if (super.getRequest().getMethod().equals("GET") && super.getRequest().hasData("id", int.class))
				status = false;

			if (super.getRequest().getMethod().equals("POST")) {
				int id = super.getRequest().getData("id", int.class);
				status = id == 0;
			}

			if (super.getRequest().hasData("aircraft", Integer.class)) {
				Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
				if (aircraftId == null)
					status2 = false;
				else if (aircraftId != 0) {
					Aircraft existingAircraft = this.repository.findAircraftById(aircraftId);
					status2 = existingAircraft != null;
				}
			}
		} catch (Throwable t) {
			status = false;
		}

		super.getResponse().setAuthorised(status && status2);

	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Technician technician;
		Date currentMoment;
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		currentMoment = MomentHelper.getCurrentMoment();

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setMoment(currentMoment);
		maintenanceRecord.setDraftMode(true);
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setStatus(MaintenaceRecordStatus.PENDING);
		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		Aircraft aircraft;
		aircraft = super.getRequest().getData("aircraft", Aircraft.class);
		super.bindObject(maintenanceRecord, "ticker", "nextInspectionDueTime", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		boolean valid = maintenanceRecord.getAircraft() != null;
		super.state(valid, "aircraft", "acme.validation.form.error.invalidAircraft");

		MaintenanceRecord existMR = this.repository.findMaintenanceRecordByTicker(maintenanceRecord.getTicker());
		boolean valid2 = existMR == null || existMR.getId() == maintenanceRecord.getId();
		super.state(valid2, "ticker", "acme.validation.form.error.duplicateTicker");

	}
	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setMoment(MomentHelper.getCurrentMoment());
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(MaintenaceRecordStatus.class, maintenanceRecord.getStatus());
		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		aircraftsCollection = this.repository.findAircrafts();

		aircrafts = SelectChoices.from(aircraftsCollection, "registrationNumber", maintenanceRecord.getAircraft());

		dataset = super.unbindObject(maintenanceRecord, "ticker", "moment", "status", "nextInspectionDueTime", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
