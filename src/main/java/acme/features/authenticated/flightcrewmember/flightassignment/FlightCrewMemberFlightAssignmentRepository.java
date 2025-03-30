
package acme.features.authenticated.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from flighAssignment fa where fa.flightCrewMember.id = memberId")
	Collection<FlightAssignment> getAllFlightAssignmentsOf(int memberId);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :memberId and fa.leg.departure >= current_timestamp")
	Collection<FlightAssignment> findPlannedAssignmentsOf(int memberId);

	@Query("select fa from flightAssignment fa where fa.id = :id")
	FlightAssignment findAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.leg.id = :legId")
	Collection<FlightAssignment> findAssignmentByLegId(int legId);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.flightCrewMember.id = :memberId and fa.leg.id <> :legId")
	boolean isCrewMemberAssignmentToOtherLeg(int memberId, int legId);

	@Query("select fcm from FlightCrewMember fcm where fcm.currentStatus = acme.entities.flightassignment.CurrentStatus.AVAILABLE")
	Collection<FlightCrewMember> findAvailableCrewMembers();

}
