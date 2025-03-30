
package acme.entities.flight;

import java.util.Date;

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
	@ManyToOne(optional = false)
	@Valid
	private Manager				manager;

	@Mandatory
	@Automapped
	@ValidString(max = 50)
	private String				tag;

	@Mandatory
	@Automapped
	@Valid
	private Boolean				indication;

	@Mandatory
	@Automapped
	@ValidMoney(min = 0.00, max = 1000000.00)
	private Money				cost;

	@Optional
	@Automapped
	@ValidString
	private String				description;


	@Transient
	public Date getDeparture() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findDeparture(this.getId()).orElse(null);
	}

	@Transient
	public Date getArrival() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findArrival(this.getId()).orElse(null);
	}

	@Transient
	public String getOrigin() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findOrigin(this.getId()).orElse("");
	}

	@Transient
	public String getDestination() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.findDestination(this.getId()).orElse("");
	}

	@Transient
	public Integer getNumberLayovers() {
		LegRepository legRepository = SpringHelper.getBean(LegRepository.class);
		return legRepository.getNumberLayovers(this.getId());
	}
}
