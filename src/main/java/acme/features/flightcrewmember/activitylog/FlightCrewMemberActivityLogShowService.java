
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		int activityLogId;

		ActivityLog activityLog;
		boolean authorised = false;
		boolean ownsIt = false;
		activityLogId = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(activityLogId);
		boolean authorised3 = this.repository.existsActivityLog(activityLogId);

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightAssignment assignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);

		if (assignment != null) {
			boolean authorised2 = this.repository.existsFlightAssignment(assignment.getId());

			boolean authorised1 = authorised3 && authorised2 && this.repository.existsFlightCrewMember(flightCrewMemberId);
			authorised = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, flightCrewMemberId);
			ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;
		}

		super.getResponse().setAuthorised(authorised && activityLog != null && ownsIt);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLog.getId());

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("masterDraftMode", flightAssignment.isDraftMode());
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
