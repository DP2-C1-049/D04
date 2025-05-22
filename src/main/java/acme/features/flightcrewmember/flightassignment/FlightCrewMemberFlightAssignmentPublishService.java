
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
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		boolean authorised = this.repository.thatFlightAssignmentIsOf(flightAssignmentId, flightCrewMemberId);
		FlightAssignment assignment;
		boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
		assignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		int legId = super.getRequest().getData("leg", int.class);
		boolean authorised3 = true;
		if (legId != 0)
			authorised3 = this.repository.existsLeg(legId);

		status = authorised3 && authorised1 && authorised && assignment.isDraftMode() && MomentHelper.isFuture(assignment.getLeg().getArrival());
		boolean isHis = assignment.getFlightCrewMember().getId() == flightCrewMemberId;

		status = status && isHis;

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
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		int id = super.getRequest().getData("id", int.class);
		assignment.setId(id);
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		assignment.setLeg(leg);
		assignment.setFlightCrewMember(flightCrewMember);
		assignment.setMoment(original.getMoment());
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		Leg leg = assignment.getLeg();
		boolean cambioDuty = !original.getDuty().equals(assignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assignment.getLeg());

		if (leg != null && cambioLeg && !this.isLegCompatible(assignment))
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(assignment);

		boolean legCompleted = this.isLegCompleted(leg);
		if (legCompleted)
			super.state(false, "leg", "acme.validation.FlightAssignment.LegAlreadyCompleted.message");
	}

	private boolean isLegCompleted(final Leg leg) {
		return leg != null && leg.getArrival() != null && leg.getArrival().before(MomentHelper.getCurrentMoment());
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

		SelectChoices currentStatus;
		SelectChoices duty;

		Collection<Leg> legs;
		SelectChoices legChoices;
		boolean isCompleted;
		int flightAssignmentId;

		flightAssignmentId = super.getRequest().getData("id", int.class);

		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		isCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignmentId, currentMoment);
		Dataset dataset;
		FlightAssignment fa = this.repository.findFlightAssignmentById(flightAssignmentId);

		legs = this.repository.findAllLegs();

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		dataset = super.unbindObject(assignment, "duty", "moment", "CurrentStatus", "remarks", "draftMode");
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getCurrentMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		dataset.put("draftMode", fa.isDraftMode());

		super.getResponse().addData(dataset);
	}
}
