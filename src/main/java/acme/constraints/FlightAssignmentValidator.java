
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.AvailabilityStatus;

@Validator
public class FlightAssignmentValidator extends AbstractValidator<ValidFlightAssignment, FlightAssignment> {

	@Override
	protected void initialise(final ValidFlightAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightAssignment flightAssignament, final ConstraintValidatorContext context) {
		if (flightAssignament == null)
			return false;
		if (flightAssignament.getFlightCrewMember() == null)
			return false;

		boolean flightCrewMemberAvailable;
		flightCrewMemberAvailable = AvailabilityStatus.AVAILABLE.equals(flightAssignament.getFlightCrewMember().getAvailabilityStatus());
		super.state(context, flightCrewMemberAvailable, "flightCrewMember", "{acme.validation.FlightAssignament.flightCrewMemberNotAvailable.message}");

		return !super.hasErrors(context);
	}

}
