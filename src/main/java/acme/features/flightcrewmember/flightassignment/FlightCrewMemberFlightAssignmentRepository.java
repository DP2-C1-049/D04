
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightcrewmembers.AvailabilityStatus;
import acme.realms.flightcrewmembers.FlightCrewMember;

@Repository
public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.leg.arrival < :moment")
	Collection<FlightAssignment> findAllFlightAssignmentByCompletedLeg(Date moment);

	@Query("select fa from FlightAssignment fa where fa.leg.arrival >= :moment")
	Collection<FlightAssignment> findAllFlightAssignmentByPlannedLeg(Date moment);

	@Query("select fa.leg from FlightAssignment fa where fa.id = :id")
	Collection<Leg> findLegsByFlightAssignmentId(int id);

	@Query("select l from Leg l where l.id  = :legId")
	Leg findLegById(int legId);

	@Query("select fa.flightCrewMember from FlightAssignment fa where fa.id = :id")
	Collection<FlightCrewMember> findFlightCrewMembersByFlightAssignmentId(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select fa.leg from FlightAssignment fa where fa.flightCrewMember.id = :id")
	Collection<Leg> findLegsByFlightCrewMember(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.availabilityStatus = :availabilityStatus")
	Collection<FlightCrewMember> findFlightCrewMembersByAvailability(AvailabilityStatus availabilityStatus);

	@Query("select fa.flightCrewMember from FlightAssignment fa where fa.leg.id = :legId")
	Collection<FlightCrewMember> findFlightCrewMembersAssignedToLeg(int legId);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int flightAssignmentId);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.leg.id = :legId and fa.duty = :duty")
	boolean existsFlightCrewMemberWithDutyInLeg(int legId, Duty duty);

	@Query("select fa from FlightAssignment fa where fa.leg.id=:id")
	Collection<FlightAssignment> findFlightAssignmentByLegId(int id);

	@Query("select case when count(fa) > 0 then true else false end " + "from FlightAssignment fa " + "where fa.id = :flightAssignmentId " + "and fa.leg.arrival < :moment")
	boolean areLegsCompletedByFlightAssignment(int flightAssignmentId, Date moment);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

}
