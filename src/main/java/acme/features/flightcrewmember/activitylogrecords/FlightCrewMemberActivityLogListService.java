
package acme.features.flightcrewmember.activitylogrecords;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);

		status = authorised && flightAssignment != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Collection<ActivityLog> activityLog;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		activityLog = this.repository.findActivityLogsByMasterId(masterId);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
		super.addPayload(dataset, activityLog, "registrationMoment", "typeOfIncident");

		super.getResponse().addData(dataset);

	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLog) {
		int masterId;

		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);

		System.out.println("El masterId es: " + masterId + " de la Assignment= " + this.repository.isFlightAssignmentAlreadyPublishedById(masterId));
		System.out.flush();
		showCreate = this.repository.associatedWithCompletedLeg(masterId, MomentHelper.getCurrentMoment());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);

	}

}
