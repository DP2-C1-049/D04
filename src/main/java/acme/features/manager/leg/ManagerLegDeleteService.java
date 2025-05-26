
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.claim.Claim;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.features.assistanceAgents.claim.ClaimRepository;
import acme.features.flightcrewmember.flightassignment.FlightCrewMemberFlightAssignmentRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository						repository;

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository	flightAssignmentRepository;

	@Autowired
	private ClaimRepository								claimRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int legId = super.getRequest().getData("id", int.class);
			Leg leg = this.repository.findLegById(legId);
			Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
			status = leg != null && leg.isDraftMode() && leg.getFlight().getManager().getId() == manager.getId();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
	}

	@Override
	public void validate(final Leg leg) {
	}

	@Override
	public void perform(final Leg leg) {
		Collection<FlightAssignment> flightAssignments;
		Collection<Claim> claims;
		flightAssignments = this.flightAssignmentRepository.findFlightAssignmentByLegId(leg.getId());
		claims = this.claimRepository.findClaimByLegId(leg.getId());
		for (Claim c : claims) {
			Collection<TrackingLog> trackingLogs;
			trackingLogs = this.claimRepository.findTrackingLogsByClaimId(c.getId());
			this.claimRepository.deleteAll(trackingLogs);
			this.claimRepository.delete(c);
		}
		for (FlightAssignment fa : flightAssignments) {
			Collection<ActivityLog> activityLogs = this.flightAssignmentRepository.findActivityLogsByFlightAssignmentId(fa.getId());
			this.flightAssignmentRepository.deleteAll(activityLogs);
			this.flightAssignmentRepository.delete(fa);
		}
		this.repository.delete(this.repository.findLegById(leg.getId()));
	}

	@Override
	public void unbind(final Leg leg) {

	}
}
