
package acme.features.flightcrewmember.flightassignment;

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
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean existsCrew = this.repository.existsFlightCrewMember(flightCrewMemberId);
		boolean existsLeg = true;
		if (super.getRequest().hasData("leg", int.class)) {
			int legId = super.getRequest().getData("leg", int.class);
			if (legId != 0)
				existsLeg = this.repository.existsLeg(legId);
		}

		boolean authorised = false;
		boolean ownsIt = false;
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		if (assignment != null) {
			boolean authorised2 = this.repository.existsFlightAssignment(assignmentId);
			boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
			authorised = authorised2 && authorised1 && this.repository.thatFlightAssignmentIsOf(assignmentId, flightCrewMemberId);
			ownsIt = assignment.getFlightCrewMember().getId() == flightCrewMemberId;
		}

		super.getResponse().setAuthorised(authorised && ownsIt && existsCrew && existsLeg);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int assignmentId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(assignmentId);
		if (super.getRequest().hasData("duty", Duty.class))
			assignment.setDuty(super.getRequest().getData("duty", Duty.class));

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());
		int assignmentId = super.getRequest().getData("id", int.class);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);
		SelectChoices currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		boolean isCompleted;
		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices dutyChoices = SelectChoices.from(Duty.class, assignment.getDuty());

		// Obtener todas las legs
		Collection<Leg> allLegs = this.repository.findAllLegs();
		FlightCrewMember crew = this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());
		Date now = MomentHelper.getCurrentMoment();

		// Filtrar las "legs" que no sean válidas según las mismas condiciones de validación
		Collection<Leg> availableLegs = allLegs.stream().filter(leg -> !leg.isDraftMode()) // La leg no puede estar en modo borrador
			.filter(leg -> !leg.getDeparture().before(now) && !leg.getArrival().before(now)) // La leg no debe haber ocurrido aún
			.filter(leg -> this.isLegCompatible(assignment, leg)) // Verificar si la leg es compatible con el miembro de la tripulación
			.filter(leg -> !this.repository.existsFlightCrewMemberWithDutyInLeg(leg.getId(), Duty.PILOT) || !Duty.PILOT.equals(assignment.getDuty())) // No debe haber un piloto si se asigna al piloto
			.filter(leg -> !this.repository.existsFlightCrewMemberWithDutyInLeg(leg.getId(), Duty.COPILOT) || !Duty.COPILOT.equals(assignment.getDuty())) // Lo mismo para copiloto
			.collect(Collectors.toList());

		//SelectChoices legChoices = null;
		// Crear el SelectChoices para las legs disponibles
		//if (availableLegs.isEmpty())
		availableLegs.add(assignment.getLeg());
		SelectChoices legChoices = SelectChoices.from(availableLegs, "flightNumber", assignment.getLeg());

		// Llenar el dataset con los datos a enviar
		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		isCompleted = this.repository.areLegsCompletedByFlightAssignment(assignmentId, currentMoment);
		dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		super.getResponse().addData(dataset);
	}

	private boolean isLegCompatible(final FlightAssignment assignment, final Leg leg) {
		// Verifica la compatibilidad de la leg con el miembro de la tripulación basado en el duty y otras reglas
		Collection<Leg> existing = this.repository.findLegsByFlightCrewMember(assignment.getFlightCrewMember().getId());
		return existing.stream().noneMatch(oldLeg -> !this.compatibleLegs2(leg, oldLeg));
	}

	private boolean compatibleLegs2(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getDeparture(), oldLeg.getDeparture(), oldLeg.getArrival()) || MomentHelper.isInRange(newLeg.getArrival(), oldLeg.getDeparture(), oldLeg.getArrival()));
	}

}
