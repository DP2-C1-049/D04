
package acme.features.assistanceAgents.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgents;

@GuiService
public class ClaimDeleteService extends AbstractGuiService<AssistanceAgents, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		try {
			if (!super.getRequest().getMethod().equals("POST"))
				super.getResponse().setAuthorised(false);
			else {
				Claim claim;
				Integer id;
				AssistanceAgents assistanceAgent;

				id = super.getRequest().getData("id", Integer.class);
				claim = this.repository.findClaimById(id);
				assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
				status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && (claim == null || claim.isDraftMode());
				super.getResponse().setAuthorised(status);
			}
		} catch (Exception e) {
			super.getResponse().setAuthorised(false);
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
	public void bind(final Claim claim) {
		super.bindObject(claim, "registrationMoment", "email", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {

	}

	@Override
	public void perform(final Claim claim) {
		Collection<TrackingLog> trackingLogs;

		trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId());
		this.repository.deleteAll(trackingLogs);
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {

	}

}
