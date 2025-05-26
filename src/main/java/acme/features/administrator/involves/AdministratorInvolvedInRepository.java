
package acme.features.administrator.involves;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Involves;
import acme.entities.task.Task;

@Repository
public interface AdministratorInvolvedInRepository extends AbstractRepository {

	@Query("select i from Involves i where i.maintenanceRecord.id = :masterId")
	Collection<Involves> findInvolvedInByMaintenanceRecord(int masterId);

	@Query("select t from Task t")
	Collection<Task> findTasksDisponibles();

	@Query("select t from Task t where t.id =:taskId ")
	Task findTaskById(int taskId);

	@Query("select i from Involves i where i.id = :id")
	Involves findInvolvedInById(int id);
	@Query("select mr from MaintenanceRecord mr where mr.id = :masterId")
	MaintenanceRecord findMaintenanceRecord(int masterId);

}
