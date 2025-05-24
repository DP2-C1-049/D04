
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.maintenanceRecord.MaintenanceRecord;

@Validator
public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	@Override
	protected void initialise(final ValidMaintenanceRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord maintenanceRecord, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (maintenanceRecord == null)
			super.state(context, false, "nextInspectionDueTime", "acme.validation.maintenanceRecord.NotNull");
		else if (maintenanceRecord.getNextInspectionDueTime() == null)
			super.state(context, false, "nextInspectionDueTime", "acme.validation.maintenanceRecord.nextInspectionNotNull");
		else {
			Date minimumNextInspectionDue;
			boolean correctNextInspectionDue;
			minimumNextInspectionDue = MomentHelper.deltaFromMoment(maintenanceRecord.getMoment(), 1, ChronoUnit.MINUTES);
			correctNextInspectionDue = MomentHelper.isAfterOrEqual(maintenanceRecord.getNextInspectionDueTime(), minimumNextInspectionDue);

			super.state(context, correctNextInspectionDue, "nextInspectionDueTime", "acme.validation.maintenanceRecord.DateCorrect");
		}
		result = !super.hasErrors(context);

		return result;
	}

}
