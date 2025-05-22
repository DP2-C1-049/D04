
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

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.id  = :legId")
	Leg findLegById(int legId);

	@Query("select fa.leg from FlightAssignment fa where fa.id = :id")
	Collection<Leg> findLegsByFlightAssignmentId(int id);

	@Query("select fa.leg from FlightAssignment fa where fa.flightCrewMember.id = :id")
	Collection<Leg> findLegsByFlightCrewMember(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.leg.arrival < :moment")
	boolean associatedWithCompletedLeg(int id, Date moment);

	@Query("select fa from FlightAssignment fa where fa.leg.arrival < :moment and fa.flightCrewMember.id = :flighCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByCompletedLeg(Date moment, int flighCrewMemberId);

	@Query("select fa from FlightAssignment fa where fa.leg.arrival >= :moment and fa.flightCrewMember.id = :flighCrewMemberId")
	Collection<FlightAssignment> findAllFlightAssignmentByPlannedLeg(Date moment, int flighCrewMemberId);

	@Query("select fa.flightCrewMember from FlightAssignment fa where fa.id = :id")
	Collection<FlightCrewMember> findFlightCrewMembersByFlightAssignmentId(int id);

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

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.id = :flightAssignmentId and fa.flightCrewMember.id = :flightCrewMemberId")
	boolean thatFlightAssignmentIsOf(int flightAssignmentId, int flightCrewMemberId);

	@Query("select case when count(fcm) > 0 then true else false end from FlightCrewMember fcm where fcm.id = :id")
	boolean existsFlightCrewMember(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id")
	boolean existsFlightAssignment(int id);

	@Query("select case when count (l) > 0 then true else false end from Leg l where l.id = :id")
	boolean existsLeg(int id);

}
