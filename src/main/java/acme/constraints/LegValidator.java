
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.authenticated.leg.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private LegRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		Flight flight = leg.getFlight();

		boolean result;

		if (leg == null || flight == null)
			super.state(context, false, "*", "acme.validation.leg.NotNull.message");
		else {

			if (leg.getArrival() != null && leg.getDeparture() != null) {
				boolean correctDepatureArrivalDate = MomentHelper.isAfterOrEqual(leg.getArrival(), leg.getDeparture());
				super.state(context, correctDepatureArrivalDate, "arrival", "acme.validation.leg.wrong-scheduled-arrival.message");
			}

			if (leg.getAircraft() != null && leg.getAircraft().getStatus() != null && leg.getAircraft().getStatus() != AircraftStatus.ACTIVE)
				super.state(context, false, "aircraft", "acme.validation.leg.aircraft-not-active.message");

		}

		result = !super.hasErrors(context);

		return result;
	}
}
