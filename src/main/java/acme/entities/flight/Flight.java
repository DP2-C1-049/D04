
package acme.entities.flight;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.leg.Leg;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

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
	@ValidMoney(min = 0)
	private Money				cost;

	@Mandatory
	@Automapped
	@ValidString
	private String				description;

	@Mandatory
	@Automapped
	@Valid
	@OneToMany
	private List<Leg>			leg;
}
