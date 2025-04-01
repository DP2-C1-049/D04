
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
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;

		flightAssignment = new FlightAssignment();

		flightAssignment.setDraftMode(true);
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

		FlightCrewMember flightCrewMember = flightAssignment.getFlightCrewMember();
		Leg leg = flightAssignment.getLeg();
		Boolean confirmation = super.getRequest().getData("confirmation", Boolean.class);
		super.state(confirmation != null && confirmation, "confirmation", "flight-crew-member.flight-assignament.form.label.confirmation");
		if (flightCrewMember != null && leg != null && this.isLegCompatible(flightAssignment)) {
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignament.FlightCrewMemberIncompatibleLegs.message");
			return;
		}
		if (leg != null)
			this.checkPilotAndCopilotAssignment(flightAssignment);
	}

	private boolean isLegCompatible(final FlightAssignment flightAssignament) {

		Collection<Leg> legsByFlightCrewMember = this.repository.findLegsByFlightCrewMember(flightAssignament.getFlightCrewMember().getId());
		Leg newLeg = flightAssignament.getLeg();

		return legsByFlightCrewMember.stream().anyMatch(existingLeg -> !this.compatibleLegs(newLeg, existingLeg));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment flightAssignment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLeg(flightAssignment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLeg(flightAssignment.getLeg().getId(), Duty.COPILOT);

		if (Duty.PILOT.equals(flightAssignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.COPILOT.equals(flightAssignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	private boolean compatibleLegs(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
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

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
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
