
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
import acme.entities.task.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int masterId;
			MaintenanceRecord maintenanceRecord;
			int technician;

			masterId = super.getRequest().getData("id", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
			technician = maintenanceRecord.getTechnician().getId();

			status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().getActiveRealm().getId() == technician;
			super.getResponse().setAuthorised(status);

			Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
			if (aircraftId == null)
				status = false;
			else if (aircraftId != 0) {
				Aircraft existingAircraft = this.repository.findAircraftById(aircraftId);
				status = status && existingAircraft != null;
			}
		}

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);
		maintenanceRecord.setMoment(currentMoment);
		maintenanceRecord.setStatus(MaintenaceRecordStatus.COMPLETED);
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

		Collection<Task> tasksInvolvedIn = this.repository.findTaskInvolvedInMaintenanceRecord(maintenanceRecord.getId());

		boolean hasPublishedTasks = tasksInvolvedIn.stream().anyMatch(task -> !task.isDraftMode());
		boolean allTasksNotDraft = tasksInvolvedIn.stream().allMatch(task -> !task.isDraftMode());

		boolean valid = !tasksInvolvedIn.isEmpty() && hasPublishedTasks && allTasksNotDraft;

		if (!valid)
			super.state(valid, "*", "acme.validation.involved-in.task");

		valid = maintenanceRecord.getAircraft() != null;
		super.state(valid, "aircraft", "acme.validation.form.error.invalidAircraft");

		MaintenanceRecord existMR = this.repository.findMaintenanceRecordByTicker(maintenanceRecord.getTicker());
		boolean valid2 = existMR == null || existMR.getId() == maintenanceRecord.getId();
		super.state(valid2, "ticker", "acme.validation.form.error.duplicateTicker");

	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setDraftMode(false);
		this.repository.save(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;
		SelectChoices choices;

		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		aircraftsCollection = this.repository.findAircrafts();

		aircrafts = SelectChoices.from(aircraftsCollection, "registrationNumber", maintenanceRecord.getAircraft());

		choices = SelectChoices.from(MaintenaceRecordStatus.class, maintenanceRecord.getStatus());
		dataset = super.unbindObject(maintenanceRecord, "ticker", "moment", "nextInspectionDueTime", "estimatedCost", "notes", "draftMode");

		dataset.put("statuses", choices);
		dataset.put("status", MaintenaceRecordStatus.COMPLETED);
		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
