
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		try {
			Integer flightAssignmentId = super.getRequest().getData("id", Integer.class);
			FlightAssignment assignment = this.repository.findFlightAssignmentById(flightAssignmentId);
			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (assignment != null) {
				boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
				boolean authorised = authorised1 && this.repository.thatFlightAssignmentIsOf(flightAssignmentId, flightCrewMemberId);
				boolean ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;
				status = assignment.isDraftMode() && authorised && ownsIt;
			}
		} catch (Exception e) {
			status = false;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		try {
			Integer flightAssignmentId = super.getRequest().getData("id", Integer.class);
			FlightAssignment assignment = null;

			if (flightAssignmentId != null)
				assignment = this.repository.findFlightAssignmentById(flightAssignmentId);

			super.getBuffer().addData(assignment);
		} catch (Exception e) {
			super.getBuffer().addData(null);
		}
	}

	@Override
	public void bind(final FlightAssignment assignment) {
	}

	@Override
	public void validate(final FlightAssignment assignment) {
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		if (assignment != null) {
			Collection<ActivityLog> activityLogs = this.repository.findActivityLogsByFlightAssignmentId(assignment.getId());
			this.repository.delete(assignment);
		}
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
	}
}
