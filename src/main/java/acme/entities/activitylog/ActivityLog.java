
package acme.entities.activitylog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityLog extends AbstractEntity {

	// Serialisation version ----------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ---------------------------------------------------------------

	//	Comentado hasta que se cree la entidad FlightCrewMember
	//	@Mandatory
	//	@Valid
	//	@Automapped
	//	@ManyToOne
	//	private FlightCrewMembers	flightCrewMember;

	//  Comentado hasta qye se cree la entidad leg
	//	@Mandatory
	//	@Valid
	//	@Automapped
	//	@ManyToOne
	//	private Leg		leg;

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
	@ValidString
	private String				description;

	@Mandatory
	@Automapped
	@ValidNumber(min = 0, max = 10)
	private Integer				severityLevel;
}
