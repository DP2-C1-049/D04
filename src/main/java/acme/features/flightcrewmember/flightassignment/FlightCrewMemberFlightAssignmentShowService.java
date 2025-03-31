
package acme.features.flightcrewmember.flightassignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);

		status = status && assignment.getFlightCrewMember().getId() == flightCrewMemberId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(assignmentId);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());

		dataset = super.unbindObject(assignment, "id", "moment", "currentStatus", "remarks");
		dataset.put("duty", assignment.getDuty().toString());
		dataset.put("dutyChoices", dutyChoices);
		dataset.put("leg.departure", assignment.getLeg().getDeparture());
		dataset.put("leg.arrival", assignment.getLeg().getArrival());

		super.getResponse().addData(dataset);
	}
}
