
package acme.features.technician.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenaceRecordStatus;
import acme.entities.maintenanceRecord.MaintenanceRecord;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {

	@Query("select count(mr) from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.status = :estado and mr.draftMode = false ")
	Integer findNumberMaintenanceRecordStatus(int technicianId, MaintenaceRecordStatus estado);

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId and mr.draftMode = false and exists (select i from Involves i where i.maintenanceRecord = mr) order by mr.nextInspectionDueTime ASC ")
	Collection<MaintenanceRecord> findRecordWithNearestInspection(int technicianId);
	@Query(" select mr.aircraft from MaintenanceRecord mr join Involves i on i.maintenanceRecord = mr where mr.draftMode = false and mr.technician.id = :technicianId group by mr.aircraft order by count(i) desc")
	Collection<Aircraft> findTop5AircraftsWithMostTasks(int technicianId);

	@Query("select avg(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.estimatedCost.currency =?3 and mr.draftMode = false and YEAR(mr.moment) = ?2")
	Double findAverageEstimatedCostLastYear(int technicianId, int currentYear, String currency);

	@Query("select min(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.estimatedCost.currency =?3 and mr.draftMode = false  and YEAR(mr.moment) = ?2")
	Double findMinimumEstimatedCostLastYear(int technicianId, int currentYear, String currency);

	@Query("select max(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.estimatedCost.currency =?3 and mr.draftMode = false  and YEAR(mr.moment) = ?2")
	Double findMaximumEstimatedCostLastYear(int technicianId, int currentYear, String currency);

	@Query("select stddev(mr.estimatedCost.amount) from MaintenanceRecord mr where mr.technician.id = ?1 and mr.estimatedCost.currency =?3 and mr.draftMode = false and YEAR(mr.moment) = ?2")
	Double findSTDDEVEstimatedCostLastYear(int technicianId, int currentYear, String currency);

	@Query("select avg(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Double findAverageEstimatedDurationTask(int technicianId);
	@Query("select min(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Integer findMinimumEstimatedDurationTask(int technicianId);
	@Query("select max(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Integer findMaximumEstimatedDurationTask(int technicianId);
	@Query("select stddev(t.estimatedDuration) from Task t where t.technician.id=:technicianId and t.draftMode = false")
	Double findSTDDEVEstimatedDurationTask(int technicianId);

}
