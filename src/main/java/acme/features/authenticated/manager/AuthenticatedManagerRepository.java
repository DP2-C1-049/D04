
package acme.features.authenticated.manager;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.Manager;

@Repository
public interface AuthenticatedManagerRepository extends AbstractRepository {

	@Query("select m from Manager m where m.userAccount.id = :id")
	Manager findManagerByUserAccountId(int id);

	@Query("select ua from UserAccount ua where ua.id = :userAccountId")
	UserAccount findUserAccountById(int userAccountId);

	@Query("SELECT a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("SELECT a from Airline a WHERE a.id = :airlineId")
	Airline findAirlineById(int airlineId);
}
