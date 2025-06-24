
package acme.features.manager.leg;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.leg.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.departure ASC")
	Collection<Leg> findLegsByflightId(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.id = :id")
	Leg findLegById(int id);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.departure ASC")
	Collection<Leg> findLegsByflightIdOrderByMoment(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId AND l.flight.manager.id = :managerId")
	Leg findOneLegByIdAndManager(int legId, int managerId);

	@Query("SELECT l FROM Leg l WHERE l.aircraft.id = :aircraftId AND l.draftMode = false AND ((:departure < l.arrival AND :arrival > l.departure))")
	Collection<Leg> findLegsWithAircraftInUse(int aircraftId, Date departure, Date arrival);
}
