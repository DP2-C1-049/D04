
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		int masterId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignament = this.repository.getFlightAssignmentById(masterId);

		super.getResponse().setAuthorised(flightAssignament != null && flightAssignament.isDraftMode());
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.getFlightAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.getLegsById(legId);

		Integer flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		super.bindObject(assignment, "duty", "moment", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment assingment) {
		FlightAssignment original = this.repository.getFlightAssignmentById(assingment.getId());
		FlightCrewMember flightCrewMember = assingment.getFlightCrewMember();
		Leg leg = assingment.getLeg();
		boolean cambioDuty = !original.getDuty().equals(assingment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assingment.getLeg());
		boolean cambioMoment = !original.getMoment().equals(assingment.getMoment());
		boolean cambioStatus = !original.getCurrentStatus().equals(assingment.getCurrentStatus());

		if (!(cambioDuty || cambioLeg || cambioMoment || cambioStatus))
			return;

		if (flightCrewMember != null && leg != null && cambioLeg && !this.isLegCompatible(assingment))
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignament.FlightCrewMemberIncompatibleLegs.message");

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(assingment);
	}

	private boolean isLegCompatible(final FlightAssignment assingment) {
		Collection<Leg> legsByMember = this.repository.findLegsByFlightCrewMember(assingment.getFlightCrewMember().getId());
		Leg newLeg = assingment.getLeg();

		return legsByMember.stream().allMatch(existingLeg -> this.areLegsCompatible(newLeg, existingLeg));
	}

	private boolean areLegsCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment assingment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLeg(assingment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLeg(assingment.getLeg().getId(), Duty.COPILOT);

		if (Duty.PILOT.equals(assingment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignament.havePilot.message");
		if (Duty.COPILOT.equals(assingment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignament.haveCopilot.message");
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs = this.repository.getAllLegs();
		Collection<FlightCrewMember> flightCrewMembers = this.repository.findFlightCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", assignment.getFlightCrewMember());
		SelectChoices currentStatus = SelectChoices.from(acme.entities.flightassignment.CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getBaseMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", flightCrewMemberChoices);

		super.getResponse().addData(dataset);
	}
}
