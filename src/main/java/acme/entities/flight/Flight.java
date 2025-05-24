
package acme.entities.flight;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.airport.Airport;
import acme.features.authenticated.leg.LegRepository;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				indication;

	@Mandatory
	@ValidMoney(min = 0)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager				manager;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	@Transient
	public Date getDeparture() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		return repository.findFirstDeparture(this.getId()).orElse(null);
	}

	@Transient
	public Date getArrival() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		return repository.findLastArrival(this.getId()).orElse(null);
	}

	@Transient
	public String getOriginCity() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		return repository.findOriginCity(this.getId()).orElse("");
	}

	@Transient
	public String getDestinationCity() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		return repository.findDestinationCity(this.getId()).orElse("");
	}

	@Transient
	public Integer getNumberOfLayovers() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		return repository.numberLayovers(this.getId());
	}

	@Transient
	public String getFlightSummary() {
		return String.format("Flight: %s → %s", this.getOriginCity(), this.getDestinationCity());
	}

	@Transient
	public Airport getDestinationAirport() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<Airport> list = repository.findOrderedDestinationAirport(this.getId());
		return list.isEmpty() ? null : list.get(0);
	}
}
