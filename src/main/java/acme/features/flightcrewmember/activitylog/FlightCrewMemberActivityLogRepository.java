
package acme.features.flightcrewmember.activitylog;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select al.flightAssignment from ActivityLog al where al.id = :id")
	FlightAssignment findFlightAssignmentByActivityLogId(int id);

	@Query("select case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id and al.flightAssignment.draftMode = false")
	boolean isFlightAssignmentAlreadyPublishedByActivityLogId(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.draftMode = false")
	boolean isFlightAssignmentAlreadyPublishedById(int id);

	@Query("select al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :masterId")
	Collection<ActivityLog> findActivityLogsByMasterId(int masterId);

	@Query("select count(al) > 0 from ActivityLog al where al.id = :activityLogId and al.flightAssignment.flightCrewMember.id = :flightCrewMemberId")
	boolean thatActivityLogIsOf(int activityLogId, int flightCrewMemberId);

	@Query("SELECT CASE WHEN COUNT(fcm) > 0 THEN true ELSE false END FROM FlightCrewMember fcm WHERE fcm.id = :id")
	boolean existsFlightCrewMember(int id);

	@Query("select case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id and al.flightAssignment.leg.arrival < :moment")
	boolean associatedWithCompletedLeg(int id, Date moment);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id and fa.leg.arrival < :moment")
	boolean flightAssignmentAssociatedWithCompletedLeg(int id, Date moment);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.id = :id")
	boolean existsFlightAssignment(int id);

	@Query("select case when count(al) > 0 then true else false end from ActivityLog al where al.id = :id")
	boolean existsActivityLog(int id);

	@Query("select case when count(fa) > 0 then true else false end from FlightAssignment fa where fa.leg.arrival < :moment and fa.id = :flightAssignmentId")
	boolean isFlightAssignmentCompleted(Date moment, int flightAssignmentId);

}
