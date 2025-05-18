
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightcrewmembers.AvailabilityStatus;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int assignmentId = super.getRequest().getData("id", int.class);

		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
		boolean authorised = authorised1 && this.repository.thatFlightAssignmentIsOf(assignmentId, flightCrewMemberId);
		boolean ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;

		super.getResponse().setAuthorised(assignment != null && assignment.isDraftMode() && authorised && ownsIt);
	}

	@Override
	public void load() {
		FlightAssignment assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		int flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		super.bindObject(assignment, "duty", "moment", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		Collection<ActivityLog> activityLogs = this.repository.findActivityLogsByFlightAssignmentId(assignment.getId());
		this.repository.deleteAll(activityLogs);
		this.repository.delete(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		SelectChoices currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());

		Collection<Leg> legs = this.repository.findAllLegs();
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		Collection<FlightCrewMember> flightCrewMembers = this.repository.findFlightCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);
		SelectChoices flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", assignment.getFlightCrewMember());

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getBaseMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", flightCrewMemberChoices);

		super.getResponse().addData(dataset);
	}
}
