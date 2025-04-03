
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		int masterId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		masterId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		technician = maintenanceRecord == null ? null : maintenanceRecord.getTechnician();

		status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(technician);

		super.getResponse().setAuthorised(status);
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
	public void bind(final MaintenanceRecord maintenanceRecord) {

		super.bindObject(maintenanceRecord, "moment", "status", "nextInspectionDueTime", //
			"estimatedCost", "notes");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {

		Collection<Task> tasksInvolvedIn = this.repository.findTaskInvolvedInMaintenanceRecord(maintenanceRecord.getId());

		boolean hasPublishedTasks = tasksInvolvedIn.stream().anyMatch(task -> !task.isDraftMode());
		boolean allTasksNotDraft = tasksInvolvedIn.stream().allMatch(task -> !task.isDraftMode());

		boolean valid = !tasksInvolvedIn.isEmpty() && hasPublishedTasks && allTasksNotDraft;

		if (!valid)
			super.state(valid, "*", "acme.validation.involves.task");
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setDraftMode(false);
		this.repository.save(maintenanceRecord);
	}
	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "moment", "status", "nextInspectionDueTime", "estimatedCost", "notes");

		super.getResponse().addData(dataset);
	}

}
