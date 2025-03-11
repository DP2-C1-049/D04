
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customers extends AbstractRole {

	//Serialisation identifier -----------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	//Atributes --------------------------------------------------------------------------

	@Mandatory
	@Automapped
	@Column(unique = true)
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	private String				identifier;

	@Mandatory
	@Automapped
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	private String				phoneNumber;

	@Mandatory
	@Automapped
	@ValidString(max = 255)
	@NotBlank
	private String				physicalAdress;

	@Mandatory
	@Automapped
	@ValidString(max = 50)
	@NotBlank
	private String				city;

	@Mandatory
	@Automapped
	@ValidString(max = 50)
	@NotBlank
	private String				country;

	@Optional
	@Automapped
	@ValidNumber(min = 0, max = 500000, integer = 6, fraction = 0)
	private Integer				earnedPoints;
}
