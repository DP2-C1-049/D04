
package acme.entities.claim;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.leg.Leg;
import acme.entities.trackingLogs.ClaimStatus;
import acme.entities.trackingLogs.TrackingLog;
import acme.features.assistanceAgents.trackingLog.TrackingLogRepository;
import acme.realms.AssistanceAgents;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private AssistanceAgents	assistanceAgent;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				email;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	public ClaimStatus getStatus() {
		TrackingLogRepository repository = SpringHelper.getBean(TrackingLogRepository.class);
		return repository.findOrderTrackingLog(this.getId()).flatMap(list -> list.stream().findFirst()).map(TrackingLog::getStatus).orElse(ClaimStatus.PENDING);
		//return ClaimStatus.PENDING;
	}


	@Mandatory
	@Valid
	@ManyToOne
	private Leg leg;
}
