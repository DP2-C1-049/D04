
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Involves;
import acme.entities.task.Task;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordByTechnicianId(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.id =:maintenanceRecordId")
	MaintenanceRecord findMaintenanceRecordById(int maintenanceRecordId);

	@Query("select a from Aircraft a where a.registrationNumber = :aircraftRegistrationNumber")
	Aircraft findAircraftByRegistrationNumber(String aircraftRegistrationNumber);

	@Query("select i from Involves i where i.maintenanceRecord.id = :id")
	Collection<Involves> findMaintenanceRecordInvolvedIn(int id);

	@Query("select i.task from Involves i where i.maintenanceRecord.id = :id")
	Collection<Task> findTaskInvolvedInMaintenanceRecord(int id);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAircrafts();

}
