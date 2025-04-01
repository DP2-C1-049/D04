
package acme.entities.trackingLogs;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.claim.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class TrackingLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Valid
	@Mandatory
	@OneToOne
	private Claim				claim;

	@ValidMoment
	@Mandatory
	@Automapped
	private Date				lastUpdateMoment;

	@ValidString(max = 50)
	@Mandatory
	@Automapped
	private String				step;

	@ValidNumber(min = 0, max = 100)
	@Mandatory
	@Automapped
	private Double				resolutionPercentage;

	@Mandatory
	@Automapped
	private boolean				indicator;

	@ValidString(max = 255)
	@Mandatory
	@Automapped
	private String				resolution;
}
