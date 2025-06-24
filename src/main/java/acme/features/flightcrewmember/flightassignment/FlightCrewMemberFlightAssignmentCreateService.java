
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
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean existsCrew = this.repository.existsFlightCrewMember(flightCrewMemberId);
		boolean existsLeg = true;
		if (super.getRequest().hasData("leg", int.class)) {
			int legId = super.getRequest().getData("leg", int.class);
			if (legId != 0)
				existsLeg = this.repository.existsLeg(legId);
		}

		super.getResponse().setAuthorised(existsCrew && existsLeg);
	}

	@Override
	public void load() {
		FlightAssignment assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setCurrentStatus(CurrentStatus.PENDING);
		assignment.setDuty(Duty.CABIN_ATTENDANT);
		assignment.setFlightCrewMember(this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		assignment.setMoment(MomentHelper.getCurrentMoment());
		assignment.setRemarks("");
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()));
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		FlightCrewMember crew = assignment.getFlightCrewMember();
		Leg leg = assignment.getLeg();
		Date now = MomentHelper.getCurrentMoment();

		if (crew != null) {
			boolean available = crew.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;
			super.state(available, "flightCrewMember", "acme.validation.FlightAssignment.flightCrewMemberNotAvailable.message");
		}

		if (leg != null) {
			super.state(!leg.isDraftMode(), "leg", "acme.validation.FlightAssignment.legDraftModeNotAllowed.message");
			boolean past = leg.getDeparture().before(now) || leg.getArrival().before(now);
			super.state(!past, "leg", "acme.validation.FlightAssignment.legAlreadyOccurred.message");
		}

		if (crew != null && leg != null && this.isLegCompatible(assignment)) {
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");
			return;
		}
		if (leg != null)
			this.checkPilotAndCopilotAssignment(assignment);
	}

	private boolean isLegCompatible(final FlightAssignment assignment) {
		Collection<Leg> existing = this.repository.findLegsByFlightCrewMember(assignment.getFlightCrewMember().getId());
		Leg candidate = assignment.getLeg();
		return existing.stream().anyMatch(oldLeg -> !this.compatibleLegs(candidate, oldLeg));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment assignment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.COPILOT);

		if (Duty.PILOT.equals(assignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.COPILOT.equals(assignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	private boolean compatibleLegs(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setMoment(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		Collection<Leg> legs = this.repository.findAllLegs();
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		FlightCrewMember crew = this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getBaseMoment());
		dataset.put("currentStatus", statusChoices);
		dataset.put("duty", dutyChoices);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", crew.getEmployeeCode());

		super.getResponse().addData(dataset);
	}
}
