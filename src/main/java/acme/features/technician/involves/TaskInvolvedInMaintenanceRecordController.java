
package acme.features.technician.involves;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.task.Involves;
import acme.realms.Technician;

@GuiController
public class TaskInvolvedInMaintenanceRecordController extends AbstractGuiController<Technician, Involves> {

	@Autowired
	private TaskInvolvedInMaintenanceRecordCreateService	createService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordDeleteService	deleteService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordListService		listService;

	@Autowired
	private TaskInvolvedInMaintenanceRecordShowService		showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}
}
