
package acme.features.assistanceAgents.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.ClaimStatus;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgents;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgents, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		try {
			boolean status;
			TrackingLog trackingLog;
			Integer id;
			AssistanceAgents assistanceAgent;
			if (!super.getRequest().getMethod().equals("GET"))
				super.getResponse().setAuthorised(false);
			else {
				id = super.getRequest().getData("id", Integer.class);
				trackingLog = null;
				if (id != null)
					trackingLog = this.repository.findTrackingLogById(id);
				assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
				status = super.getRequest().getPrincipal().hasRealm(assistanceAgent);
				super.getResponse().setAuthorised(status);
			}
		} catch (Exception e) {
			super.getResponse().setAuthorised(false);
		}
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		//System.out.println(trackingLog.getStep());
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

		SelectChoices statusChoices;
		Claim claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());

		Dataset dataset;

		statusChoices = SelectChoices.from(ClaimStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode");
		dataset.put("statusChoices", statusChoices);

		dataset.put("claimId", claim.getId());

		super.getResponse().addData(dataset);

	}

}
