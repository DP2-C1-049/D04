
package acme.features.authenticated.flightassignment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("SELECT COUNT(fa) FROM FlightAssignment fa WHERE fa.flightAssignment.id = :flighAssignmentId")
	Integer getFlightAssignmentById(Integer flightAssignmentId);
}
