
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

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
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int assignmentId = super.getRequest().getData("id", int.class);
		boolean authorised = this.repository.thatFlightAssignmentIsOf(assignmentId, flightCrewMemberId);
		boolean status;
		FlightAssignment assignment;
		boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
		assignment = this.repository.findFlightAssignmentById(assignmentId);
		status = authorised1 && authorised && assignment.isDraftMode() && MomentHelper.isFuture(assignment.getLeg().getArrival());
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		assignment = new FlightAssignment();

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		Integer legId;
		Leg leg;
		Integer flightCrewMemberId;
		FlightCrewMember flightCrewMember;
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		super.bindObject(assignment, "duty", "moment", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		FlightCrewMember flightCrewMember = assignment.getFlightCrewMember();
		Leg leg = assignment.getLeg();
		boolean cambioFlightCrewMember = !original.getFlightCrewMember().equals(flightCrewMember);
		boolean cambioDuty = !original.getDuty().equals(assignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assignment.getLeg());
		boolean cambioMoment = !original.getMoment().equals(assignment.getMoment());
		boolean cambioStatus = !original.getCurrentStatus().equals(assignment.getCurrentStatus());

		if (!(cambioDuty || cambioLeg || cambioMoment || cambioStatus))
			return;

		if (flightCrewMember != null && leg != null && cambioLeg && !this.isLegCompatible(assignment))
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");

		if (leg != null && (cambioDuty || cambioLeg || cambioFlightCrewMember))
			this.checkPilotAndCopilotAssignment(assignment);

		boolean legCompleted = this.repository.areLegsCompletedByFlightAssignment(assignment.getId(), MomentHelper.getCurrentMoment());

		if (legCompleted)
			super.state(false, "leg", "acme.validation.FlightAssignment.LegAlreadyCompleted.message");
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
		if (this.huboAlgunCambio(assignment))
			assignment.setMoment(MomentHelper.getCurrentMoment());
		assignment.setDraftMode(false);

		this.repository.save(assignment);
	}

	private boolean huboAlgunCambio(final FlightAssignment assignment) {
		boolean cambio = false;
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		FlightCrewMember flightCrewMember = assignment.getFlightCrewMember();
		boolean cambioFlightCrewMember = !original.getFlightCrewMember().equals(flightCrewMember);
		boolean cambioDuty = !original.getDuty().equals(assignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assignment.getLeg());
		boolean cambioStatus = !original.getCurrentStatus().equals(assignment.getCurrentStatus());
		boolean cambioRemarks = false;
		if (original.getRemarks() != null)
			cambioRemarks = !original.getRemarks().equals(assignment.getRemarks());
		else if (assignment.getRemarks() != null)
			cambioRemarks = !assignment.getRemarks().equals(original.getRemarks());
		cambio = cambioDuty || cambioFlightCrewMember || cambioLeg || cambioStatus || cambioRemarks;
		return cambio;
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		SelectChoices currentStatus;
		SelectChoices duty;

		Collection<Leg> legs;
		SelectChoices legChoices;

		Collection<FlightCrewMember> flightCrewMembers;
		SelectChoices flightCrewMemberChoices;
		Dataset dataset;

		legs = this.repository.findAllLegs();
		flightCrewMembers = this.repository.findFlightCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", assignment.getFlightCrewMember());

		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());

		dataset = super.unbindObject(assignment, "duty", "moment", "CurrentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getCurrentMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", flightCrewMemberChoices);

		super.getResponse().addData(dataset);
	}
}
