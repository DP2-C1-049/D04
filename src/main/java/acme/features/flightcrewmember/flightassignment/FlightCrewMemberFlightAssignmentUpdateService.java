
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
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(masterId);

		super.getResponse().setAuthorised(flightAssignment != null && flightAssignment.isDraftMode());
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(id);
		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		Integer flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		super.bindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks");
		flightAssignment.setLeg(leg);
		flightAssignment.setFlightCrewMember(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(flightAssignment.getId());
		FlightCrewMember flightCrewMember = flightAssignment.getFlightCrewMember();
		Leg leg = flightAssignment.getLeg();
		boolean cambioDuty = !original.getDuty().equals(flightAssignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(flightAssignment.getLeg());
		boolean cambioMoment = !original.getMoment().equals(flightAssignment.getMoment());
		boolean cambioStatus = !original.getCurrentStatus().equals(flightAssignment.getCurrentStatus());

		if (!(cambioDuty || cambioLeg || cambioMoment || cambioStatus))
			return;

		if (flightCrewMember != null && leg != null && cambioLeg && !this.isLegCompatible(flightAssignment))
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(flightAssignment);
	}

	private boolean isLegCompatible(final FlightAssignment flightAssignment) {
		Collection<Leg> legsByMember = this.repository.findLegsByFlightCrewMember(flightAssignment.getFlightCrewMember().getId());
		Leg newLeg = flightAssignment.getLeg();

		return legsByMember.stream().allMatch(existingLeg -> this.areLegsCompatible(newLeg, existingLeg));
	}

	private boolean areLegsCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment flightAssignment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLeg(flightAssignment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLeg(flightAssignment.getLeg().getId(), Duty.COPILOT);

		if (Duty.PILOT.equals(flightAssignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.COPILOT.equals(flightAssignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Collection<Leg> legs = this.repository.findAllLegs();
		Collection<FlightCrewMember> flightCrewMembers = this.repository.findFlightCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		SelectChoices flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());
		SelectChoices currentStatus = SelectChoices.from(acme.entities.flightassignment.CurrentStatus.class, flightAssignment.getCurrentStatus());
		SelectChoices duty = SelectChoices.from(Duty.class, flightAssignment.getDuty());

		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
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
