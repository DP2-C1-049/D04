
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :memberId")
	Collection<FlightAssignment> getAllFlightAssignmentsOf(@Param("memberId") int memberId);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment getFlightAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :memberId and fa.leg.departure >= current_timestamp")
	Collection<FlightAssignment> findPlannedAssignmentsOf(@Param("memberId") int memberId);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :memberId and fa.leg.departure < current_timestamp")
	Collection<FlightAssignment> findCompletedAssignmentsOf(@Param("memberId") int memberId);

	@Query("select fa from FlightAssignment fa join fetch fa.leg join fetch fa.flightCrewMember where fa.id = :id")
	FlightAssignment findAssignmentById(@Param("id") int id);

	@Query("select fa from FlightAssignment fa where fa.leg.id = :legId")
	Collection<FlightAssignment> findAssignmentByLegId(@Param("legId") int legId);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.flightCrewMember.id = :memberId and fa.leg.id <> :legId")
	boolean isCrewMemberAssignmentToOtherLeg(@Param("memberId") int memberId, @Param("legId") int legId);

	@Query("select fcm from FlightCrewMember fcm where fcm.availabilityStatus = acme.realms.flightcrewmembers.AvailabilityStatus.AVAILABLE")
	Collection<FlightCrewMember> findAvailableCrewMembers();
}
