
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> flightAssignments;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		String type = (String) super.getRequest().getData().get("type");

		if (type != null && type.equalsIgnoreCase("planned"))
			flightAssignments = this.repository.findPlannedAssignmentsOf(flightCrewMemberId);
		else if (type != null && type.equalsIgnoreCase("completed"))
			flightAssignments = this.repository.findCompletedAssignmentsOf(flightCrewMemberId);
		else
			flightAssignments = this.repository.getAllFlightAssignmentsOf(flightCrewMemberId);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "id", "duty", "moment", "currentStatus", "remarks", "leg.departure", "leg.arrival");
		super.getResponse().addData(dataset);
	}
}
