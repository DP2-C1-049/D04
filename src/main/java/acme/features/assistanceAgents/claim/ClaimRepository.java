
package acme.features.assistanceAgents.claim;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.leg.Leg;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface ClaimRepository extends AbstractRepository {

	//Deberia comentarse
	//@Query("SELECT DISTINCT c FROM Claim c JOIN TrackingLog t ON t.claim.id = c.id WHERE t.lastUpdateMoment = (SELECT MAX(t2.lastUpdateMoment) FROM TrackingLog t2 WHERE t2.claim = c) AND (t.status != 'PENDING' AND c.assistanceAgent.id = :agentId)")
	//List<Claim> findCompletedClaimsByAssistanceAgent(int agentId);
	//Deberia comentarse
	//@Query("SELECT DISTINCT c FROM Claim c JOIN TrackingLog t ON t.claim.id = c.id WHERE t.lastUpdateMoment = (SELECT MAX(t2.lastUpdateMoment) FROM TrackingLog t2 WHERE t2.claim = c) AND (t.status = 'PENDING' AND c.assistanceAgent.id = :agentId)")
	//List<Claim> findPendingClaimsByAssistanceAgent(int agentId);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id=:agentId")
	List<Claim> findClaimsByAssistanceAgent(int agentId);

	@Query("SELECT c FROM Claim c WHERE c.id=:claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLeg();

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

}
