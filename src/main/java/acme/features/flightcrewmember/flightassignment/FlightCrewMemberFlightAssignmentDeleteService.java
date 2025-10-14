
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (assignment != null) {
			boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
			boolean authorised = authorised1 && this.repository.thatFlightAssignmentIsOf(flightAssignmentId, flightCrewMemberId);
			boolean ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;
			status = assignment.isDraftMode() && authorised && ownsIt;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		// No necesitamos bind para delete, ya que no estamos modificando datos
		// Solo necesitamos la entidad cargada para eliminarla
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		// Validación simple: asegurarnos de que la asignación existe y está en draft mode
		if (assignment == null)
			super.state(false, "*", "acme.validation.flightassignment.notfound.message");
		else if (!assignment.isDraftMode())
			super.state(false, "*", "acme.validation.flightassignment.notDraft.message");
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		if (assignment != null) {
			// Eliminar los activity logs asociados primero
			Collection<ActivityLog> activityLogs = this.repository.findActivityLogsByFlightAssignmentId(assignment.getId());
			if (activityLogs != null && !activityLogs.isEmpty())
				this.repository.deleteAll(activityLogs);
			// Eliminar la flight assignment
			this.repository.delete(assignment);
		}
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		// No necesitamos unbind para delete
	}
}
