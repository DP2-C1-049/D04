
package acme.entities.leg;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@Automapped
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@Mandatory
	@Automapped
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@Mandatory
	@Automapped
	@ValidNumber
	private Integer				duration;

	@Mandatory
	@Automapped
	@Valid
	private LegStatus				status;

	@Mandatory
	@Valid
	@ManyToOne
	private Airport				departureAirport;

	@Mandatory
	@Valid
	@ManyToOne
	private Airport				arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne
	private Aircraft			aircraft;

	@Mandatory
	@ManyToOne
	@Valid
	private Flight				flight;
}
