
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentPlannedListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> assignments;

		Date currentMoment;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		currentMoment = MomentHelper.getCurrentMoment();
		assignments = this.repository.findAllFlightAssignmentByPlannedLeg(currentMoment, flightCrewMemberId);

		super.getBuffer().addData(assignments);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");

		super.getResponse().addData(dataset);
	}
}
