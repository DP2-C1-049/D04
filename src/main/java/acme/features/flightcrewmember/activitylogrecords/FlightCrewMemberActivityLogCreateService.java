
package acme.features.flightcrewmember.activitylogrecords;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment assignment;

		masterId = super.getRequest().getData("masterId", int.class);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);

		assignment = this.repository.findFlightAssignmentById(masterId);
		status = authorised && assignment != null;
		boolean ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;

		super.getResponse().setAuthorised(status);
		super.getResponse().setAuthorised(status && ownsIt);

	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int masterId;
		FlightAssignment assignment;

		masterId = super.getRequest().getData("masterId", int.class);
		assignment = this.repository.findFlightAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setFlightAssignment(assignment);
		activityLog.setDraftMode(true);
		activityLog.setDescription("");
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setSeverityLevel(0);
		activityLog.setTypeOfIncident("");

		super.getBuffer().addData(activityLog);

	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");

	}

	@Override
	public void validate(final ActivityLog activityLog) {

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put("masterId", masterId);

		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("readonly", false);
		dataset.put("masterDraftMode", !this.repository.isFlightAssignmentAlreadyPublishedById(masterId));
		super.getResponse().addData(dataset);

	}

}
