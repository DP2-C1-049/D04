
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
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
@Table(indexes = {
	@Index(columnList = "customer_id"), @Index(columnList = "locatorCode")
})
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

	@Optional
	@ValidString(min = 4, max = 4, pattern = "[0-9]{4}")
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
		Money res = new Money();
		if (this.getFlight() != null) {
			Money flightCost = this.getFlight().getCost();
			BookingRepository bookingRepository = SpringHelper.getBean(BookingRepository.class);
			Integer numberOfPassengers = bookingRepository.findAllPassengersByBookingId(this.getId());

			res.setCurrency(flightCost.getCurrency());
			res.setAmount(flightCost.getAmount() * numberOfPassengers);
			return res;
		}
		res.setAmount(0.0);
		res.setCurrency("EUR");
		return res;

	}
}
