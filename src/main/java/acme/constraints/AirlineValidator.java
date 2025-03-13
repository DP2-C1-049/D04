
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;

@Validator
public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AirlineRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAirline annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {
		// HINT: Airline can be null
		assert context != null;

		boolean result;

		if (airline == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean uniqueAirline;
			Airline existingAirline;

			existingAirline = this.repository.findAirlineByIATACode(airline.getIATACode());

			uniqueAirline = existingAirline == null || existingAirline.equals(airline);

			super.state(context, uniqueAirline, "IATACode", "acme.validation.Airline.duplicated-IATACode.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
