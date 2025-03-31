
package acme.features.flightcrewmember.flightassignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.getFlightAssignmentById(flightAssignmentId);

		super.getResponse().setAuthorised(flightCrewMemberId == assignment.getFlightCrewMember().getId());
	}

	@Override
	public void load() {

		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.getFlightAssignmentById(id);

		super.getBuffer().addData(assignment);

	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "leg", "duty", "remarks");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		if (assignment.getLeg() != null && assignment.getLeg().getDeparture() != null) {
			boolean validLeg = assignment.getLeg().getDeparture().after(MomentHelper.getCurrentMoment());
			super.state(validLeg, "leg", "flightassignment.create.error.pastLeg");
		}
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());
		dataset = super.unbindObject(assignment, "leg", "duty", "remarks", "moment", "currentStatus", "id");
		dataset.put("dutyChoices", dutyChoices);

		super.getResponse().addData(dataset);
	}
}
