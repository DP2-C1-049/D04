
package acme.features.authenticated.technician;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.realms.Technician;

@Repository
public interface AuthenticatedTechnicianRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :userAccountId")
	UserAccount findUserAccountById(int userAccountId);

	@Query("select t from Technician t where t.userAccount.id = :userAccountId")
	Technician findTechnicianByUserAccountId(int userAccountId);

}
