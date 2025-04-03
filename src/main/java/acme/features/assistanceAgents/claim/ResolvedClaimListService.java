
package acme.features.assistanceAgents.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.ClaimStatus;
import acme.realms.AssistanceAgents;

@GuiService
public class ResolvedClaimListService extends AbstractGuiService<AssistanceAgents, Claim> {

	@Autowired
	private ClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgents.class);

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		Collection<Claim> claims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findClaimsByAssistanceAgent(assistanceAgentId).stream().filter(x -> x.getStatus() != ClaimStatus.PENDING).toList();
		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {

		Dataset dataset;
		ClaimStatus indicator = claim.getStatus();

		dataset = super.unbindObject(claim, "email", "type");
		dataset.put("indicator", indicator);
		super.addPayload(dataset, claim, "registrationMoment", "description", "leg.flightNumber");

		super.getResponse().addData(dataset);
	}
}
