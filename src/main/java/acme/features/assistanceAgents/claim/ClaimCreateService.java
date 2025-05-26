
package acme.features.assistanceAgents.claim;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.realms.AssistanceAgents;

@GuiService
public class ClaimCreateService extends AbstractGuiService<AssistanceAgents, Claim> {

	@Autowired
	private ClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		try {
			status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgents.class);

			if (super.getRequest().hasData("id")) {
				Integer legId = super.getRequest().getData("leg", Integer.class);
				if (legId == null || legId != 0) {
					Leg leg = this.repository.findLegByLegId(legId);
					status = status && leg != null && !leg.isDraftMode();
				}
			}
		} catch (Exception e) {
			status = false;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		Date registrationMoment;
		AssistanceAgents assistanceAgent = (AssistanceAgents) super.getRequest().getPrincipal().getActiveRealm();

		registrationMoment = MomentHelper.getCurrentMoment();

		claim = new Claim();
		claim.setRegistrationMoment(registrationMoment);
		claim.setEmail("");
		claim.setDescription("");
		claim.setAssistanceAgent(assistanceAgent);
		claim.setType(ClaimType.FLIGHT_ISSUES);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "registrationMoment", "email", "description", "type", "leg", "id");

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
		Date registrationMoment;

		registrationMoment = MomentHelper.getCurrentMoment();
		claim.setRegistrationMoment(registrationMoment);
		claim.setDraftMode(true);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		List<Leg> legs = new ArrayList<>();
		SelectChoices choices;
		SelectChoices choices2;
		Dataset dataset;

		choices = SelectChoices.from(ClaimType.class, claim.getType());
		for (Leg leg : this.repository.findAllLegPublish())
			if (leg.getArrival().before(claim.getRegistrationMoment()))
				legs.add(leg);
		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "leg", "id");
		dataset.put("readonly", false);
		dataset.put("types", choices);
		dataset.put("legs", choices2);

		super.getResponse().addData(dataset);
	}

}
