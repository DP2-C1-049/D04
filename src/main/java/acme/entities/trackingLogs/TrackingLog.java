
package acme.entities.trackingLogs;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.claim.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(indexes = {
	@Index(columnList = "claim_id"), @Index(columnList = "claim_id,draftMode")
})
public class TrackingLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private Claim				claim;

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@ValidMoment(past = true)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				step;

	@Mandatory
	@ValidNumber(min = 0, max = 100)
	@Automapped
	private Double				resolutionPercentage;

	@Mandatory
	@Automapped
	@Valid
	private ClaimStatus			status;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				resolution;
}
