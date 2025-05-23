
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Involves;
import acme.entities.task.Task;

@Repository
public interface TaskInvolvedInMaintenanceRecordRepository extends AbstractRepository {

	@Query("select m from MaintenanceRecord m where m.id = :masterId")
	MaintenanceRecord findMaintenanceRecordById(int masterId);

	@Query("select t from Task t where t.id = :taskId")
	Task findTaskById(int taskId);

	@Query("select t from Task t")
	Collection<Task> findTasksDisponibles();

	@Query("select t from Task t where t.draftMode = false and t.technician.id = :technicianId ")
	Collection<Task> findTasksDisponiblesByTechnicianId(int technicianId);

	@Query("select i from Involves i where i.id = :id")
	Involves findInvolvedInById(int id);

	@Query("select i from Involves i where i.maintenanceRecord.id = :masterId")
	Collection<Involves> findInvolvedInByMaintenanceRecord(int masterId);

	@Query("select i.task from Involves i where i.maintenanceRecord.id = :id")
	Collection<Task> findAllInvolvedInMaintenanceRecord(int id);
	@Query("select i.task from Involves i where i.maintenanceRecord.id = :id and i.task.id = :taskId")
	Task findInvolvedInMaintenanceRecordTask(int id, int taskId);
	@Query("select i from Involves i where i.task.id=:id and i.maintenanceRecord.id=:id2")
	Involves findInvolvedInByTaskIdAndMaintenanceRecordId(int id, int id2);
	@Query("select t from Task t where t.draftMode = false or t.technician.id = :technicianId ")
	Collection<Task> findTasksRelacion(int technicianId);
	@Query("select i from Involves i where i.maintenanceRecord.id = :masterId and i.task.id = :taskId")
	Involves findInvolvedInTMR(int masterId, Integer taskId);
}
