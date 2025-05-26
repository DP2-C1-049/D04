
package acme.features.flightcrewmember.activitylog;

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
		boolean status = false;
		int masterId;
		FlightAssignment assignment;
		if (super.getRequest().hasData("masterId", int.class)) {

			masterId = super.getRequest().getData("masterId", int.class);
			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);

			assignment = this.repository.findFlightAssignmentById(masterId);
			boolean authorised2 = false;
			if (assignment != null) {
				authorised2 = this.repository.existsFlightAssignment(masterId);
				status = authorised && authorised2;
				boolean ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;

				status = status && ownsIt && this.repository.isFlightAssignmentCompleted(MomentHelper.getCurrentMoment(), masterId);
			}
		}
		super.getResponse().setAuthorised(status);

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
