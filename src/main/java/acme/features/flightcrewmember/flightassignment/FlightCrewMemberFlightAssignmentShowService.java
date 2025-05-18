
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightcrewmembers.AvailabilityStatus;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int assignmentId = super.getRequest().getData("id", int.class);
		boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
		boolean authorised = authorised1 && this.repository.thatFlightAssignmentIsOf(assignmentId, flightCrewMemberId);
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs;
		SelectChoices legChoices;

		Collection<FlightCrewMember> flightCrewMembers;
		SelectChoices flightCrewMemberChoices;

		Dataset dataset;

		SelectChoices currentStatus;
		int assignmentId;

		assignmentId = super.getRequest().getData("id", int.class);
		SelectChoices duty;

		boolean isCompleted;
		legs = this.repository.findAllLegs();
		flightCrewMembers = this.repository.findFlightCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", assignment.getFlightCrewMember());

		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		isCompleted = this.repository.areLegsCompletedByFlightAssignment(assignmentId, currentMoment);
		dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", flightCrewMemberChoices);
		dataset.put("isCompleted", isCompleted);
		System.out.println("es completa? " + this.repository.areLegsCompletedByFlightAssignment(assignmentId, MomentHelper.getCurrentMoment()));
		super.getResponse().addData(dataset);
	}

}
