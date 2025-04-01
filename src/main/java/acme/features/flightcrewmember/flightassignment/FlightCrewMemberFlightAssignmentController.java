
package acme.features.flightcrewmember.flightassignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiController
public class FlightCrewMemberFlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentCompletedListService	completedListService;

	@Autowired
	private FlightCrewMemberFlightAssignmentPlannedListService		plannedListService;

	@Autowired
	private FlightCrewMemberFlightAssignmentShowService				showService;

	@Autowired
	private FlightCrewMemberFlightAssignmentCreateService			createService;

	@Autowired
	private FlightCrewMemberFlightAssignmentDeleteService			deleteService;

	@Autowired
	private FlightCrewMemberFlightAssignmentUpdateService			updateService;

	@Autowired
	private FlightCrewMemberFlightAssignmentPublishService			publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("completed-list", "list", this.completedListService);
		super.addCustomCommand("planned-list", "list", this.plannedListService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
