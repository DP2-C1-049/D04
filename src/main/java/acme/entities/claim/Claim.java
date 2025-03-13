
package acme.entities.claim;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.realms.AssistanceAgents;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Valid
	@Mandatory
	@ManyToOne
	private AssistanceAgents	assistanceAgent;

	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
	private Date				registrationMoment;

	@ValidEmail
	@Mandatory
	@Automapped
	private String				email;

	@ValidString(max = 255)
	@Mandatory
	@Automapped
	private String				description;

	@Valid
	@Mandatory
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Automapped
	private Boolean				indicator;
}
