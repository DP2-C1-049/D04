
package acme.entities.review;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Review extends AbstractEntity {

	//Serialisation identifier -----------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	//Atributes --------------------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				moment;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				subject;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				text;

	@Optional
	@ValidNumber(min = 0, max = 10, fraction = 2)
	@Automapped
	private Double				score;

	@Optional
	@Valid
	@Automapped
	private Boolean				recommended;

}
