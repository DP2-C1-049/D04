
package acme.features.assistanceAgents.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgents;

@GuiService
public class AssistanceAgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgents, TrackingLog> {

	@Autowired
	private TrackingLogRepository repository;


	@Override
	public void authorise() {
		try {
			boolean status;
			TrackingLog trackingLog;
			Integer id;
			AssistanceAgents assistanceAgent;
			if (!super.getRequest().getMethod().equals("POST"))
				super.getResponse().setAuthorised(false);
			else {
				id = super.getRequest().getData("id", Integer.class);
				trackingLog = null;
				if (id != null)
					trackingLog = this.repository.findTrackingLogById(id);
				assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
				status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && (trackingLog == null || trackingLog.isDraftMode());
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

		super.getBuffer().addData(trackingLog);
	}
	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		;
	}

	@Override
	public void perform(final TrackingLog trackingLog) {

		this.repository.delete(trackingLog);
	}
	@Override
	public void unbind(final TrackingLog trackingLog) {

	}
}
