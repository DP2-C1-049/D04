
package acme.features.flightcrewmember.flightassignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		try {
			Integer assignmentId = super.getRequest().getData("id", Integer.class);

			if (assignmentId != null) {
				FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);

				if (assignment != null) {
					boolean principalIsOwner = assignment.getFlightCrewMember().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
					boolean isDraft = assignment.isDraftMode();
					status = principalIsOwner && isDraft;
				}
			}
		} catch (Exception e) {
			status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		try {
			Integer assignmentId = super.getRequest().getData("id", Integer.class);
			FlightAssignment assignment = null;

			if (assignmentId != null)
				assignment = this.repository.findFlightAssignmentById(assignmentId);

			super.getBuffer().addData(assignment);
		} catch (Exception e) {
			super.getBuffer().addData(null);
		}
	}

	@Override
	public void bind(final FlightAssignment assignment) {
	}

	@Override
	public void validate(final FlightAssignment assignment) {

	}

	@Override
	public void perform(final FlightAssignment assignment) {
		if (assignment != null) {
			assignment.setDraftMode(false);
			this.repository.save(assignment);
		}
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

	}

}
