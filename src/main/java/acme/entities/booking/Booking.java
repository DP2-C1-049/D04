
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.flight.Flight;
import acme.features.authenticated.booking.BookingRepository;
import acme.realms.Customer;
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
	private Flight				flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer			customer;

	//Attributes --------------------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Mandatory
	@ValidMoney(min = 0)
	@Automapped
	private Money				price;

	@Optional
	@ValidString(min = 4, max = 4)
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	//ENUM --------------------------------
	public enum TravelClass {
		ECONOMY, BUSINESS
	}


	@Transient
	public Money getPrice() {
		Money flightCost = this.getFlight().getCost();
		BookingRepository bookingRepository = SpringHelper.getBean(BookingRepository.class);
		Integer numberOfPassengers = bookingRepository.getNumberPassengersOfBooking(this.getId());
		Money res = new Money();
		res.setCurrency(flightCost.getCurrency());
		res.setAmount(flightCost.getAmount() * numberOfPassengers);
		return res;
	}

}
