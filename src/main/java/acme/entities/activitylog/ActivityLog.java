
package acme.entities.activitylog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.flight.Flight;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.AssistanceAgents;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityLog extends AbstractEntity {

	// Serialisation version ----------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ---------------------------------------------------------------

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	FlightAssignment			flightAssignment;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@Automapped
	@ValidString(min = 1, max = 50)
	private String				typeOfIncident;

	@Mandatory
	@Automapped
	@ValidString(min = 0, max = 255)
	private String				description;

	@Mandatory
	@Automapped
	@ValidNumber(min = 0, max = 10, fraction = 0)
	private Integer				severityLevel;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgents	assistanceAgent;
}
