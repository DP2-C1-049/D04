
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
public class FlightCrewMemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		boolean principalIsOwner = assignment.getFlightCrewMember().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
		super.getResponse().setAuthorised(principalIsOwner && assignment.isDraftMode());
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		assignment.setLeg(leg);
		FlightCrewMember crew = this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());
		assignment.setFlightCrewMember(crew);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		FlightCrewMember crew = assignment.getFlightCrewMember();
		Leg leg = assignment.getLeg();
		Date now = MomentHelper.getCurrentMoment();

		boolean cambioDuty = !original.getDuty().equals(assignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assignment.getLeg());
		boolean cambioMoment = !original.getMoment().equals(assignment.getMoment());
		boolean cambioStatus = !original.getCurrentStatus().equals(assignment.getCurrentStatus());

		if (!(cambioDuty || cambioLeg || cambioMoment || cambioStatus))
			return;

		if (crew != null && (cambioDuty || cambioLeg)) {
			boolean available = crew.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;
			super.state(available, "flightCrewMember", "acme.validation.FlightAssignment.flightCrewMemberNotAvailable.message");
		}

		if (leg != null && cambioLeg) {
			super.state(!leg.isDraftMode(), "leg", "acme.validation.FlightAssignment.legDraftModeNotAllowed.message");
			boolean past = leg.getDeparture().before(now) || leg.getArrival().before(now);
			super.state(!past, "leg", "acme.validation.FlightAssignment.legAlreadyOccurred.message");

			if (crew != null && this.isLegIncompatible(assignment)) {
				super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");
				return;
			}
		}

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(assignment);
	}

	private boolean isLegIncompatible(final FlightAssignment assignment) {
		Collection<Leg> existing = this.repository.findLegsByFlightCrewMember(assignment.getFlightCrewMember().getId());
		Leg candidate = assignment.getLeg();
		return existing.stream().anyMatch(oldLeg -> !this.areLegsCompatible(candidate, oldLeg));
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
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs = this.repository.findAllLegs();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		Date currentMoment = MomentHelper.getCurrentMoment();
		boolean isCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignmentId, currentMoment);

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		FlightCrewMember crew = this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("readonly", false);
		dataset.put("moment", currentMoment);
		dataset.put("currentStatus", statusChoices);
		dataset.put("duty", dutyChoices);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", crew.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		dataset.put("draftMode", assignment.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
