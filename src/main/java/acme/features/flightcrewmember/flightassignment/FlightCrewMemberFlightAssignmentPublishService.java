
package acme.features.flightcrewmember.flightassignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
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
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);

		if (assignment == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		boolean principalIsOwner = assignment.getFlightCrewMember().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean isDraft = assignment.isDraftMode();

		super.getResponse().setAuthorised(principalIsOwner && isDraft);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		// No necesitamos bind porque no estamos modificando los datos
		// Solo vamos a cambiar el draftMode
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		// Validación mínima: solo verificar que existe y está en draft mode
		// Las demás validaciones ya se hicieron en create/update
		if (assignment == null)
			super.state(false, "*", "acme.validation.flightassignment.notfound.message");
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		// Solo cambiar el draftMode a false
		assignment.setDraftMode(false);
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		// Solo necesitamos unbind básico
		Dataset dataset = super.unbindObject(assignment, "draftMode");
		dataset.put("draftMode", assignment.isDraftMode());
		super.getResponse().addData(dataset);
	}
}
