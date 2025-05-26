
package acme.features.technician.dashboard;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenaceRecordStatus;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.forms.TechnicianDashboard;
import acme.realms.Technician;

@GuiService
public class TechnicianDashboardShowService extends AbstractGuiService<Technician, TechnicianDashboard> {

	@Autowired
	private TechnicianDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TechnicianDashboard dashboard;
		int technicianId = this.getRequest().getPrincipal().getActiveRealm().getId();

		Integer numberMaintenanceRecordPending;
		Integer numberMaintenanceRecordInProgress;
		Integer numberMaintenanceRecordCompleted;
		MaintenanceRecord recordWithNearestInspection;
		List<Aircraft> top5AircraftsWithMostTasks;
		Double averageEstimatedCostLastYearEUR;
		Double minimumEstimatedCostLastYearEUR;
		Double maximumEstimatedCostLastYearEUR;
		Double sTDDEVEstimatedCostLastYearEUR;

		Double averageEstimatedCostLastYearUSD;
		Double minimumEstimatedCostLastYearUSD;
		Double maximumEstimatedCostLastYearUSD;
		Double sTDDEVEstimatedCostLastYearUSD;

		Double averageEstimatedCostLastYearGBP;
		Double minimumEstimatedCostLastYearGBP;
		Double maximumEstimatedCostLastYearGBP;
		Double sTDDEVEstimatedCostLastYearGBP;

		Double averageEstimatedDurationTask;
		Integer minimumEstimatedDurationTask;
		Integer maximumEstimatedDurationTask;
		Double sTDDEVEstimatedDurationTask;
		int currentYear = LocalDate.now().getYear() - 1;

		numberMaintenanceRecordPending = this.repository.findNumberMaintenanceRecordStatus(technicianId, MaintenaceRecordStatus.PENDING);
		numberMaintenanceRecordInProgress = this.repository.findNumberMaintenanceRecordStatus(technicianId, MaintenaceRecordStatus.IN_PROGRESS);
		numberMaintenanceRecordCompleted = this.repository.findNumberMaintenanceRecordStatus(technicianId, MaintenaceRecordStatus.COMPLETED);

		Collection<MaintenanceRecord> records = this.repository.findRecordWithNearestInspection(technicianId);
		recordWithNearestInspection = records.isEmpty() ? null : records.iterator().next();
		top5AircraftsWithMostTasks = this.repository.findTop5AircraftsWithMostTasks(technicianId).stream().limit(5).collect(Collectors.toList());

		averageEstimatedCostLastYearEUR = this.repository.findAverageEstimatedCostLastYear(technicianId, currentYear, "EUR");
		minimumEstimatedCostLastYearEUR = this.repository.findMinimumEstimatedCostLastYear(technicianId, currentYear, "EUR");
		maximumEstimatedCostLastYearEUR = this.repository.findMaximumEstimatedCostLastYear(technicianId, currentYear, "EUR");
		sTDDEVEstimatedCostLastYearEUR = this.repository.findSTDDEVEstimatedCostLastYear(technicianId, currentYear, "EUR");

		averageEstimatedCostLastYearUSD = this.repository.findAverageEstimatedCostLastYear(technicianId, currentYear, "USD");
		minimumEstimatedCostLastYearUSD = this.repository.findMinimumEstimatedCostLastYear(technicianId, currentYear, "USD");
		maximumEstimatedCostLastYearUSD = this.repository.findMaximumEstimatedCostLastYear(technicianId, currentYear, "USD");
		sTDDEVEstimatedCostLastYearUSD = this.repository.findSTDDEVEstimatedCostLastYear(technicianId, currentYear, "USD");

		averageEstimatedCostLastYearGBP = this.repository.findAverageEstimatedCostLastYear(technicianId, currentYear, "GBP");
		minimumEstimatedCostLastYearGBP = this.repository.findMinimumEstimatedCostLastYear(technicianId, currentYear, "GBP");
		maximumEstimatedCostLastYearGBP = this.repository.findMaximumEstimatedCostLastYear(technicianId, currentYear, "GBP");
		sTDDEVEstimatedCostLastYearGBP = this.repository.findSTDDEVEstimatedCostLastYear(technicianId, currentYear, "GBP");

		averageEstimatedDurationTask = this.repository.findAverageEstimatedDurationTask(technicianId);
		minimumEstimatedDurationTask = this.repository.findMinimumEstimatedDurationTask(technicianId);
		maximumEstimatedDurationTask = this.repository.findMaximumEstimatedDurationTask(technicianId);
		sTDDEVEstimatedDurationTask = this.repository.findSTDDEVEstimatedDurationTask(technicianId);

		dashboard = new TechnicianDashboard();
		dashboard.setNumberMaintenanceRecordPending(numberMaintenanceRecordPending);
		dashboard.setNumberMaintenanceRecordInProgress(numberMaintenanceRecordInProgress);
		dashboard.setNumberMaintenanceRecordCompleted(numberMaintenanceRecordCompleted);
		dashboard.setRecordWithNearestInspection(recordWithNearestInspection);
		dashboard.setTop5AircraftsWithMostTasks(top5AircraftsWithMostTasks);
		dashboard.setAverageEstimatedCostLastYearEUR(averageEstimatedCostLastYearEUR);
		dashboard.setMinimumEstimatedCostLastYearEUR(minimumEstimatedCostLastYearEUR);
		dashboard.setMaximumEstimatedCostLastYearEUR(maximumEstimatedCostLastYearEUR);
		dashboard.setSTDDEVEstimatedCostLastYearEUR(sTDDEVEstimatedCostLastYearEUR);

		dashboard.setAverageEstimatedCostLastYearUSD(averageEstimatedCostLastYearUSD);
		dashboard.setMinimumEstimatedCostLastYearUSD(minimumEstimatedCostLastYearUSD);
		dashboard.setMaximumEstimatedCostLastYearUSD(maximumEstimatedCostLastYearUSD);
		dashboard.setSTDDEVEstimatedCostLastYearUSD(sTDDEVEstimatedCostLastYearUSD);

		dashboard.setAverageEstimatedCostLastYearGBP(averageEstimatedCostLastYearGBP);
		dashboard.setMinimumEstimatedCostLastYearGBP(minimumEstimatedCostLastYearGBP);
		dashboard.setMaximumEstimatedCostLastYearGBP(maximumEstimatedCostLastYearGBP);
		dashboard.setSTDDEVEstimatedCostLastYearGBP(sTDDEVEstimatedCostLastYearGBP);

		dashboard.setAverageEstimatedDurationTask(averageEstimatedDurationTask);
		dashboard.setMinimumEstimatedDurationTask(minimumEstimatedDurationTask);
		dashboard.setMaximumEstimatedDurationTask(maximumEstimatedDurationTask);
		dashboard.setSTDDEVEstimatedDurationTask(sTDDEVEstimatedDurationTask);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final TechnicianDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, //
			"numberMaintenanceRecordPending", "numberMaintenanceRecordInProgress", "numberMaintenanceRecordCompleted", //
			"recordWithNearestInspection", "top5AircraftsWithMostTasks", "averageEstimatedCostLastYearEUR", //
			"minimumEstimatedCostLastYearEUR", "maximumEstimatedCostLastYearEUR", "sTDDEVEstimatedCostLastYearEUR",//
			"averageEstimatedCostLastYearUSD", //
			"minimumEstimatedCostLastYearUSD", "maximumEstimatedCostLastYearUSD", "sTDDEVEstimatedCostLastYearUSD",//
			"averageEstimatedCostLastYearGBP", //
			"minimumEstimatedCostLastYearGBP", "maximumEstimatedCostLastYearGBP", "sTDDEVEstimatedCostLastYearGBP",//
			"averageEstimatedDurationTask", "minimumEstimatedDurationTask", "maximumEstimatedDurationTask",//
			"sTDDEVEstimatedDurationTask");

		super.getResponse().addData(dataset);
	}
}
