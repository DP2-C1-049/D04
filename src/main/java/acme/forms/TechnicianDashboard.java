
package acme.forms;

import java.util.Collection;

import acme.client.components.basis.AbstractForm;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	Integer						numberMaintenanceRecordPending;
	Integer						numberMaintenanceRecordInProgress;
	Integer						numberMaintenanceRecordCompleted;
	MaintenanceRecord			recordWithNearestInspection;
	Collection<Aircraft>		top5AircraftsWithMostTasks;

	Double						averageEstimatedCostLastYearEUR;
	Double						minimumEstimatedCostLastYearEUR;
	Double						maximumEstimatedCostLastYearEUR;
	Double						sTDDEVEstimatedCostLastYearEUR;

	Double						averageEstimatedCostLastYearUSD;
	Double						minimumEstimatedCostLastYearUSD;
	Double						maximumEstimatedCostLastYearUSD;
	Double						sTDDEVEstimatedCostLastYearUSD;

	Double						averageEstimatedCostLastYearGBP;
	Double						minimumEstimatedCostLastYearGBP;
	Double						maximumEstimatedCostLastYearGBP;
	Double						sTDDEVEstimatedCostLastYearGBP;

	Double						averageEstimatedDurationTask;
	Integer						minimumEstimatedDurationTask;
	Integer						maximumEstimatedDurationTask;
	Double						sTDDEVEstimatedDurationTask;

}
