
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.claim.Claim;
import acme.entities.flight.Flight;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.features.assistanceAgents.claim.ClaimRepository;
import acme.features.flightcrewmember.flightassignment.FlightCrewMemberFlightAssignmentRepository;
import acme.features.manager.leg.ManagerLegRepository;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository						repository;

	@Autowired
	private ManagerLegRepository						legRepository;

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
			int flightId = super.getRequest().getData("id", int.class);
			Flight flight = this.repository.findById(flightId);
			Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

			status = flight != null && flight.isDraftMode() && flight.getManager().getId() == manager.getId();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
	}

	@Override
	public void perform(final Flight flight) {
		Integer flightId = flight.getId();
		Collection<FlightAssignment> flightAssignments;
		Collection<Claim> claims;
		Collection<Leg> legs = this.legRepository.findLegsByflightId(flight.getId());
		if (legs == null)
			throw new IllegalStateException("legRepository.findLegsByflightId(" + flight.getId() + ") returned NULL");
		for (Leg leg : legs) {
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
			this.legRepository.delete(leg);
		}
		this.repository.deleteById(flightId);
	}

	@Override
	public void unbind(final Flight flight) {
	}
}
