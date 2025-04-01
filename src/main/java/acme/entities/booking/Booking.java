
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.flight.Flight;
import acme.realms.Customers;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booking extends AbstractEntity {

	//Serialisation identifier -----------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	//Relations --------------------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customers			customers;

	//Attributes --------------------------------------------------------------------------

	@Mandatory
	@Column(unique = true)
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	private String				locatorCode;

	@Mandatory
	@Automapped
	@ValidMoment(past = true)
	private Date				purchaseMoment;

	@Mandatory
	@Automapped
	@Valid
	private TravelClass			travelClass;

	@Mandatory
	@Automapped
	@ValidMoney(min = 0)
	private Money				price;

	@Optional
	@Automapped
	@ValidString(min = 4, max = 4)
	private String				lastNibble;


	//ENUM --------------------------------
	public enum TravelClass {
		ECONOMY, BUSINESS
	}


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight flight;
}
