
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
public class FlightCrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;
		boolean ownsIt = false;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		try {
			Integer assignmentId = super.getRequest().getData("id", Integer.class);

			if (assignmentId != null) {
				FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
				if (assignment != null) {
					boolean authorised2 = this.repository.existsFlightAssignment(assignmentId);
					boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
					authorised = authorised2 && authorised1 && this.repository.thatFlightAssignmentIsOf(assignmentId, flightCrewMemberId);
					ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;
				}
			}
		} catch (Exception e) {
			authorised = false;
			ownsIt = false;
		}

		super.getResponse().setAuthorised(authorised && ownsIt);
	}

	@Override
	public void load() {
		FlightAssignment assignment = null;

		try {
			Integer id = super.getRequest().getData("id", Integer.class);

			if (id != null)
				assignment = this.repository.findFlightAssignmentById(id);
		} catch (Exception e) {
		}

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		Collection<Leg> legs;
		SelectChoices legChoices;
		SelectChoices currentStatus;
		SelectChoices duty;

		boolean isCompleted;
		legs = this.repository.findAllLegs();

		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());

		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();

		int assignmentId = assignment.getId();
		isCompleted = this.repository.areLegsCompletedByFlightAssignment(assignmentId, currentMoment);

		String dutyLabel = duty.getSelected().getLabel();
		String currentStatusLabel = currentStatus.getSelected().getLabel();
		String legLabel = legChoices.getSelected().getLabel();

		dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);

		dataset.put("dutyLabel", dutyLabel);
		dataset.put("currentStatusLabel", currentStatusLabel);
		dataset.put("legLabel", legLabel);

		super.getResponse().addData(dataset);
	}
}
