
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
		boolean status;
		int masterId;
		FlightAssignment flightAssignment;
		masterId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		status = flightAssignment.isDraftMode() && MomentHelper.isFuture(flightAssignment.getLeg().getArrival());
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		Integer legId;
		Leg leg;
		Integer flightCrewMemberId;
		FlightCrewMember flightCrewMember;
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		flightCrewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

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

		boolean legCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignment.getId(), MomentHelper.getCurrentMoment());

		if (legCompleted)
			super.state(false, "leg", "acme.validation.FlightAssignment.LegAlreadyCompleted.message");
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
		flightAssignment.setDraftMode(false);
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {

		SelectChoices currentStatus;
		SelectChoices duty;

		Collection<Leg> legs;
		SelectChoices legChoices;

		Collection<FlightCrewMember> flightCrewMembers;
		SelectChoices flightCrewMemberChoices;
		Dataset dataset;

		legs = this.repository.findAllLegs();
		flightCrewMembers = this.repository.findFlightCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		currentStatus = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, flightAssignment.getDuty());

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "CurrentStatus", "remarks", "draftMode");
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
