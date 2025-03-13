
package acme.entities.airline;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAirline;
import acme.constraints.ValidIATACode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidAirline
public class Airline extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(max = 50)
	@Mandatory
	@Automapped
	private String				name;

	@ValidIATACode
	@Mandatory
	@Automapped
	private String				IATACode;

	@ValidUrl
	@Mandatory
	@Automapped
	private String				website;

	@Valid
	@Mandatory
	@Automapped
	private AirlineType			type;

	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
	private Date				foundationMoment;

	@ValidEmail
	@Optional
	@Automapped
	private String				email;

	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Optional
	@Automapped
	private String				phoneNumber;
}
