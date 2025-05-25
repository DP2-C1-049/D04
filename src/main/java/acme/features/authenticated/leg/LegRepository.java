
package acme.features.authenticated.leg;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport.Airport;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT COUNT(l) - 1 FROM Leg l WHERE l.flight.id = :flightId")
	Integer numberLayovers(@Param("flightId") int flightId);

	@Query("SELECT MIN(l.departure) FROM Leg l WHERE l.flight.id = :flightId")
	Optional<Date> findFirstDeparture(@Param("flightId") int flightId);

	@Query("SELECT MAX(l.arrival) FROM Leg l WHERE l.flight.id = :flightId")
	Optional<Date> findLastArrival(@Param("flightId") int flightId);

	@Query("SELECT l.departureAirport.city FROM Leg l WHERE l.flight.id= :flightId ORDER BY l.departure ASC")
	List<String> findOriginCity(@Param("flightId") int flightId);

	@Query("SELECT l.arrivalAirport.city FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.arrival DESC")
	List<String> findDestinationCity(@Param("flightId") int flightId);

	@Query("SELECT l.departureAirport.iataCode FROM Leg l WHERE l.flight.id = :flightId AND l.departure = (SELECT MIN(l2.departure) FROM Leg l2 WHERE l2.flight.id = :flightId)")
	Optional<String> findOriginIataCode(@Param("flightId") int flightId);

	@Query("SELECT l.arrivalAirport.iataCode FROM Leg l WHERE l.flight.id = :flightId AND l.arrival = (SELECT MAX(l2.arrival) FROM Leg l2 WHERE l2.flight.id = :flightId)")
	Optional<String> findDestinationIataCode(@Param("flightId") int flightId);

	@Query("SELECT l.departureAirport FROM Leg l WHERE l.flight.id = :flightId AND l.departure = (SELECT MIN(l2.departure) FROM Leg l2 WHERE l2.flight.id = :flightId)")
	List<Airport> findOrderedDestinationAirport(@Param("flightId") int flightId);
}
