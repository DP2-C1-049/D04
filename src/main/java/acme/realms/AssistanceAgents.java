
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AssistanceAgents extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Mandatory
	@Column(unique = true)
	private String				employeeCode;

	@ValidString(max = 255)
	@Mandatory
	@Automapped
	private String				spokenLenguages;

	@Valid
	@Mandatory
	@ManyToOne(optional = false)
	private Airline				airline;

	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Mandatory
	@Automapped
	private Date				dateStarted;

	@ValidString(max = 255)
	@Optional
	@Automapped
	private String				bio;

	@ValidMoney
	@Mandatory
	@Automapped
	private Money				salary;

	@ValidUrl
	@Optional
	@Automapped
	private String				photoLink;

}
