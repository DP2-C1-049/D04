
package acme.features.assistanceAgents.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgents;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgents, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		Integer claimId;
		Claim claim;
		if (!super.getRequest().getMethod().equals("GET"))
			super.getResponse().setAuthorised(false);
		else {

			claimId = super.getRequest().getData("claimId", Integer.class);
			claim = this.repository.getClaimById(claimId);
			status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

			super.getResponse().setAuthorised(status);
		}

	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;
		int assistanceAgentId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (super.getRequest().getData().isEmpty())
			trackingLogs = this.repository.findAllTrackingLogs(assistanceAgentId);
		else {
			int claimId = super.getRequest().getData("claimId", int.class);
			trackingLogs = this.repository.findTrackingLogsByClaimId(claimId);
		}

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		int claimId = super.getRequest().getData("claimId", int.class);

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "resolutionPercentage", "status", "step", "resolution");
		super.getResponse().addGlobal("claimId", claimId);

		super.getResponse().addData(dataset);
	}

}
