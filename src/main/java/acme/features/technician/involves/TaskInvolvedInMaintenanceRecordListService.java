
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Involves;
import acme.realms.Technician;

@GuiService
public class TaskInvolvedInMaintenanceRecordListService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		status = maintenanceRecord != null && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId;
		Collection<Involves> involvedIn;

		masterId = super.getRequest().getData("masterId", int.class);
		involvedIn = this.repository.findInvolvedInByMaintenanceRecord(masterId);
		super.getBuffer().addData(involvedIn);
	}

	@Override
	public void unbind(final Involves involvedIn) {
		Dataset dataset;

		dataset = super.unbindObject(involvedIn);
		dataset.put("task", involvedIn.getTask().getDescription());
		dataset.put("priority", involvedIn.getTask().getPriority());
		super.addPayload(dataset, involvedIn);
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Involves> involvedIns) {
		int masterId;
		MaintenanceRecord maintenanceRecord;
		final boolean showCreate;
		masterId = super.getRequest().getData("masterId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		showCreate = maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
