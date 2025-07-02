
package acme.features.assistanceAgents.claim;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.entities.trackingLogs.ClaimStatus;
import acme.realms.AssistanceAgents;

@GuiService
public class ClaimShowService extends AbstractGuiService<AssistanceAgents, Claim> {

	@Autowired
	private ClaimRepository repository;


	@Override
	public void authorise() {

		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);
		else {
			boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgents.class);

			super.getResponse().setAuthorised(status);

			int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer claimId = super.getRequest().getData("id", Integer.class);
			if (claimId == null)
				super.getResponse().setAuthorised(false);
			else {
				Claim claim = this.repository.findClaimById(claimId);

				super.getResponse().setAuthorised(agentId == claim.getAssistanceAgent().getId());
			}
		}

	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		List<Leg> legs = new ArrayList<>();

		//		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices choices2;
		Dataset dataset;
		ClaimStatus indicator;

		indicator = claim.getStatus();
		choices = SelectChoices.from(ClaimType.class, claim.getType());

		legs = this.repository.findAllLegPublish().stream().filter(l -> l.getArrival().before(claim.getRegistrationMoment())).toList();
		//legs = this.repository.findAllLegPublish();
		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "draftMode", "id");
		dataset.put("types", choices);
		dataset.put("leg", choices2.getSelected().getKey());
		dataset.put("legs", choices2);
		dataset.put("indicator", indicator);

		super.getResponse().addData(dataset);
	}

}
