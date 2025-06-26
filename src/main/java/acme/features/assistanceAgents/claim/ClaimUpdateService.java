
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
import acme.realms.AssistanceAgents;

@GuiService
public class ClaimUpdateService extends AbstractGuiService<AssistanceAgents, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);
		boolean status = claim.isDraftMode();
		AssistanceAgents agent;
		agent = (AssistanceAgents) super.getRequest().getPrincipal().getActiveRealm();
		status = status && claim.getAssistanceAgent().equals(agent);
		String method = super.getRequest().getMethod();
		if (method.equals("POST")) {

			int legId = super.getRequest().getData("leg", int.class);
			status = status && (this.repository.findLegByLegId(legId) != null || legId == 0 || this.repository.findLegByLegId(claim.getLeg().getId()) != null);

		}
		super.getResponse().setAuthorised(status);
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
	public void bind(final Claim claim) {
		super.bindObject(claim, "email", "description", "type", "leg", "id");
	}

	@Override
	public void validate(final Claim claim) {
		boolean valid;
		if (claim.getLeg() != null && claim.getRegistrationMoment() != null) {
			valid = claim.getRegistrationMoment().after(claim.getLeg().getArrival());
			super.state(valid, "leg", "assistanceAgent.claim.form.error.badLeg");
		}
	}
	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {

		List<Leg> legs = new ArrayList<>();
		SelectChoices choices;
		SelectChoices choices2;
		Dataset dataset;

		choices = SelectChoices.from(ClaimType.class, claim.getType());
		legs = this.repository.findAllLegPublish().stream().filter(l -> l.getArrival().before(claim.getRegistrationMoment())).toList();
		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "draftMode", "id");

		dataset.put("types", choices);
		dataset.put("leg", choices2);
		dataset.put("legs", choices2);

		super.getResponse().addData(dataset);
	}

}
