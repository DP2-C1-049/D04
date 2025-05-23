
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
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
		boolean authorised2 = true;
		if (super.getRequest().hasData("leg", int.class)) {
			int legId = super.getRequest().getData("leg", int.class);
			if (legId != 0)
				authorised2 = this.repository.existsLeg(legId);
		}

		boolean authorised = authorised1 && authorised2;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightAssignment assignment;

		assignment = new FlightAssignment();

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
		Integer legId;
		Leg leg;

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(flightCrewMember);
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		FlightCrewMember flightCrewMember = assignment.getFlightCrewMember();
		Leg leg = assignment.getLeg();

		if (flightCrewMember != null && leg != null && this.isLegCompatible(assignment)) {
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");
			return;
		}
		if (leg != null)
			this.checkPilotAndCopilotAssignment(assignment);
	}

	private boolean isLegCompatible(final FlightAssignment assignment) {

		Collection<Leg> legsByFlightCrewMember = this.repository.findLegsByFlightCrewMember(assignment.getFlightCrewMember().getId());
		Leg newLeg = assignment.getLeg();

		return legsByFlightCrewMember.stream().anyMatch(existingLeg -> !this.compatibleLegs(newLeg, existingLeg));
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

		SelectChoices currentStatus;
		SelectChoices duty;

		Collection<Leg> legs;
		SelectChoices legChoices;

		Dataset dataset;

		legs = this.repository.findAllLegs();

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);
		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());

		dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getBaseMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());

		super.getResponse().addData(dataset);
	}
}
