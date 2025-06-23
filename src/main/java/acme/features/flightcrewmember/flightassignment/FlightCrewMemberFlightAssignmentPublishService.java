
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
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int flightAssignmentId = super.getRequest().getData("id", int.class);
			boolean authorised = this.repository.thatFlightAssignmentIsOf(flightAssignmentId, flightCrewMemberId);
			FlightAssignment assignment = this.repository.findFlightAssignmentById(flightAssignmentId);
			boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
			int legId = super.getRequest().getData("leg", int.class);
			boolean authorised3 = true;
			if (legId != 0)
				authorised3 = this.repository.existsLeg(legId);

			boolean ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;
			status = authorised3 && authorised1 && authorised && assignment.isDraftMode() && ownsIt;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment = new FlightAssignment();
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		int id = super.getRequest().getData("id", int.class);
		FlightAssignment original = this.repository.findFlightAssignmentById(id);
		assignment.setId(id);
		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(flightCrewMember);
		assignment.setMoment(original.getMoment());
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		FlightCrewMember crew = assignment.getFlightCrewMember();
		Leg leg = assignment.getLeg();
		Date now = MomentHelper.getCurrentMoment();
		boolean cambioDuty = !original.getDuty().equals(assignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assignment.getLeg());

		if (!(cambioDuty || cambioLeg))
			return;

		if (crew != null && (cambioDuty || cambioLeg)) {
			boolean available = crew.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;
			super.state(available, "flightCrewMember", "acme.validation.FlightAssignment.flightCrewMemberNotAvailable.message");
		}

		if (leg != null && cambioLeg) {
			super.state(!leg.isDraftMode(), "leg", "acme.validation.FlightAssignment.legDraftModeNotAllowed.message");
			boolean past = leg.getDeparture().before(now) || leg.getArrival().before(now);
			super.state(!past, "leg", "acme.validation.FlightAssignment.legAlreadyOccurred.message");

			if (!this.isLegCompatible(assignment)) {
				super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");
				return;
			}
		}

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(assignment);

	}

	private boolean isLegCompatible(final FlightAssignment assignment) {
		Collection<Leg> legsByMember = this.repository.findLegsByFlightCrewMember(assignment.getFlightCrewMember().getId());
		Leg newLeg = assignment.getLeg();
		return legsByMember.stream().allMatch(existingLeg -> this.areLegsCompatible(newLeg, existingLeg));
	}

	private boolean areLegsCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment assignment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.COPILOT);

		if (Duty.PILOT.equals(assignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.COPILOT.equals(assignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setMoment(MomentHelper.getCurrentMoment());
		assignment.setDraftMode(false);

		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs = this.repository.findAllLegs();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		Date currentMoment = MomentHelper.getCurrentMoment();
		boolean isCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignmentId, currentMoment);

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("readonly", false);
		dataset.put("moment", currentMoment);
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		dataset.put("draftMode", assignment.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
