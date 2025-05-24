
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {
		if (activityLog == null)
			return false;
		FlightAssignment flightAssignament = activityLog.getFlightAssignment();
		if (activityLog.getRegistrationMoment() == null || flightAssignament == null)
			return false;
		Leg leg = flightAssignament.getLeg();
		if (leg == null || leg.getArrival() == null)
			return false;
		Date activityLogMoment = activityLog.getRegistrationMoment();
		Date scheduledArrival = leg.getArrival();
		Boolean activityLogMomentIsAfterscheduledArrival = MomentHelper.isAfter(activityLogMoment, scheduledArrival);
		if (!activityLog.isDraftMode())
			super.state(context, activityLogMomentIsAfterscheduledArrival, "WrongActivityLogDate", "{acme.validation.activityLog.wrongMoment.message}");

		return !super.hasErrors(context);

	}

}
