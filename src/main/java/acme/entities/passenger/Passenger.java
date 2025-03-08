
package acme.entities.passenger;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Passenger extends AbstractEntity {

	//Serialisation identifier -----------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	//Atributes --------------------------------------------------------------------------

	//	Commented until creating the consumer entity
	//	@Mandatory
	//	@Automapped
	//	@Valid
	//	@ManyToOne
	//	private Booking				booking;

	@Mandatory
	@Automapped
	@NotBlank
	@ValidString(max = 255)
	private String				fullName;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				email;

	@Mandatory
	@Automapped
	@ValidString(pattern = "^[A-Z0-9]{6,9}$")
	private String				passportNumber;

	@Mandatory
	@Automapped
	@ValidMoment(past = true)
	private Date				dateOfBirth;

	@Optional
	@Automapped
	@ValidString(max = 50)
	private String				specialNeeds;
}
