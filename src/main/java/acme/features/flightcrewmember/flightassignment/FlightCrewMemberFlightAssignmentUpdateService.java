
package acme.features.flightcrewmember.flightassignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

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
		boolean status = false;

		Integer assignmentId = null;
		try {
			assignmentId = super.getRequest().getData("id", Integer.class);
		} catch (Exception e) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (assignmentId == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		if (assignment == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		boolean principalIsOwner = assignment.getFlightCrewMember().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean isDraft = assignment.isDraftMode();

		if (!principalIsOwner || !isDraft) {
			super.getResponse().setAuthorised(false);
			return;
		}

		boolean correctDuty = true;
		if (super.getRequest().hasData("duty"))
			try {
				super.getRequest().getData("duty", Duty.class);
			} catch (Exception ex) {
				correctDuty = false;
			}

		status = correctDuty;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment = null;

		Integer assignmentId = null;
		try {
			assignmentId = super.getRequest().getData("id", Integer.class);
		} catch (Exception e) {
			super.getBuffer().addData(null);
			return;
		}

		if (assignmentId != null)
			assignment = this.repository.findFlightAssignmentById(assignmentId);

		super.getBuffer().addData(assignment);
	}
	@Override
	public void bind(final FlightAssignment assignment) {
		Duty duty = null;
		if (super.getRequest().hasData("duty", Duty.class))
			duty = super.getRequest().getData("duty", Duty.class);

		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = null;
		if (legId != 0)
			leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "currentStatus", "remarks");

		assignment.setDuty(duty);
		assignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		if (assignment.getDuty() == null || assignment.getLeg() == null)
			return;

		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());
		FlightCrewMember crew = assignment.getFlightCrewMember();

		boolean cambioDuty = !original.getDuty().equals(assignment.getDuty());
		boolean cambioLeg = !original.getLeg().equals(assignment.getLeg());
		boolean cambioStatus = !original.getCurrentStatus().equals(assignment.getCurrentStatus());

		if (!(cambioDuty || cambioLeg || cambioStatus))
			return;

		if (crew != null && (cambioDuty || cambioLeg)) {
			boolean available = crew.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;
			super.state(available, "flightCrewMember", "acme.validation.FlightAssignment.flightCrewMemberNotAvailable.message");
		}

		if (cambioDuty || cambioLeg)
			this.checkPilotAndCopilotAssignment(assignment);
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment assignment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLegExcludingAssignment(assignment.getLeg().getId(), Duty.PILOT, assignment.getId());
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLegExcludingAssignment(assignment.getLeg().getId(), Duty.COPILOT, assignment.getId());

		if (Duty.PILOT.equals(assignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.COPILOT.equals(assignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		if (assignment.getDuty() == null || assignment.getLeg() == null)
			return;

		assignment.setMoment(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Duty currentDuty = assignment.getDuty();
		if (super.getRequest().hasData("duty", Duty.class)) {
			Duty newDuty = super.getRequest().getData("duty", Duty.class);
			currentDuty = newDuty;
		}

		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, currentDuty);

		Collection<Leg> availableLegs;
		Date now = MomentHelper.getCurrentMoment();

		Duty selectedDuty = currentDuty;

		if (selectedDuty == null)
			availableLegs = new ArrayList<>();
		else if (selectedDuty == Duty.PILOT)
			availableLegs = this.repository.findAvailableLegsWithoutPilot(now);
		else if (selectedDuty == Duty.COPILOT)
			availableLegs = this.repository.findAvailableLegsWithoutCopilot(now);
		else
			availableLegs = this.repository.findAvailableLegs(now);

		FlightCrewMember crew = assignment.getFlightCrewMember();
		Collection<Leg> existingLegs = this.repository.findLegsByFlightCrewMember(crew.getId());

		Collection<Leg> compatibleLegs = availableLegs.stream().filter(leg -> this.isLegCompatibleWithExisting(leg, existingLegs)).collect(Collectors.toList());

		Leg currentLeg = assignment.getLeg();
		if (currentLeg != null && !compatibleLegs.contains(currentLeg))
			compatibleLegs.add(currentLeg);

		SelectChoices legChoices = SelectChoices.from(compatibleLegs, "flightNumber", assignment.getLeg());

		int flightAssignmentId = super.getRequest().getData("id", int.class);
		Date currentMoment = MomentHelper.getCurrentMoment();
		boolean isCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignmentId, currentMoment);

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
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

	private boolean isLegCompatibleWithExisting(final Leg candidate, final Collection<Leg> existingLegs) {
		return existingLegs.stream().allMatch(existingLeg -> this.legCompatible(candidate, existingLeg));
	}

	private boolean legCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}
}
