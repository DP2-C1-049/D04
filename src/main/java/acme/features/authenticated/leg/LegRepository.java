
package acme.features.authenticated.leg;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import acme.client.repositories.AbstractRepository;

public interface LegRepository extends AbstractRepository {

	@Query("SELECT COUNT(l) - 1 FROM Leg l WHERE l.flight.id = :flightId")
	Integer getNumberLayovers(@Param("flightId") int flightId);

	@Query(value = "SELECT l.departure FROM leg l WHERE l.flight_id = :flightId ORDER BY l.departure ASC LIMIT 1", nativeQuery = true)
	Optional<Date> findDeparture(@Param("flightId") int flightId);

	@Query(value = "SELECT l.arrival FROM leg l WHERE l.flight_id = :flightId ORDER BY l.arrival DESC LIMIT 1", nativeQuery = true)
	Optional<Date> findArrival(@Param("flightId") int flightId);

	@Query(value = "SELECT l.departure_airport FROM leg l WHERE l.flight_id = :flightId ORDER BY l.departure ASC LIMIT 1", nativeQuery = true)
	Optional<String> findOrigin(@Param("flightId") int flightId);

	@Query(value = "SELECT l.arrival_airport FROM leg l WHERE l.flight_id = :flightId ORDER BY l.arrival DESC LIMIT 1", nativeQuery = true)
	Optional<String> findDestination(@Param("flightId") int flightId);
}
