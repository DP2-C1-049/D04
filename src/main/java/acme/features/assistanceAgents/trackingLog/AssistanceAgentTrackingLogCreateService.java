
package acme.features.assistanceAgents.trackingLog;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLogs.ClaimStatus;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgents;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgents, TrackingLog> {

	@Autowired
	private TrackingLogRepository repository;

	/*
	 * @Override
	 * public void authorise() {
	 * boolean status;
	 * Integer claimId;
	 * Claim claim;
	 * 
	 * claimId = super.getRequest().getData("claimId", Integer.class);
	 * claim = null;
	 * if (claimId != null)
	 * claim = this.repository.getClaimById(claimId);
	 * status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());
	 * 
	 * super.getResponse().setAuthorised(status);
	 * 
	 * }
	 */


	@Override
	public void authorise() {
		boolean status;
		Integer claimId;
		Claim claim;

		try {
			claimId = super.getRequest().getData("claimId", Integer.class);
			claim = null;
			if (claimId != null)
				claim = this.repository.getClaimById(claimId);
			status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

			super.getResponse().setAuthorised(status);

		} catch (Exception e) {
			status = false;
		}
		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		TrackingLog trackingLog;
		int claimId = super.getRequest().getData("claimId", int.class);
		Claim claim = this.repository.getClaimById(claimId);

		trackingLog = new TrackingLog();
		trackingLog.setDraftMode(true);
		trackingLog.setClaim(claim);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");

	}

	@Override
	public void validate(final TrackingLog trackingLog) {

		boolean valid;
		//valid = trackingLog.getResolutionPercentage() != null;
		//super.state(valid, "ResolutionPercentage", "assistanceAgent.trackingLog.form.error.cantBeNull");

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolutionPercentage() < 100.0) {
			valid = trackingLog.getStatus().equals(ClaimStatus.PENDING);
			super.state(valid, "status", "assistanceAgent.trackingLog.form.error.badStatus");
		} else if (trackingLog.getStatus() != null) {
			valid = !trackingLog.getStatus().equals(ClaimStatus.PENDING);
			super.state(valid, "status", "assistanceAgent.trackingLog.form.error.badStatus2");
		}
		if (trackingLog.getStatus() != null && trackingLog.getStatus().equals(ClaimStatus.PENDING)) {
			valid = trackingLog.getResolution() == null || trackingLog.getResolution().isBlank();
			super.state(valid, "resolution", "assistanceAgent.trackingLog.form.error.badResolution");
		} else {
			valid = trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank();
			super.state(valid, "resolution", "assistanceAgent.trackingLog.form.error.badResolution2");
		}
		if (trackingLog.getClaim() != null) {
			TrackingLog highestTrackingLog;
			Optional<List<TrackingLog>> trackingLogs = this.repository.findOrderTrackingLog(trackingLog.getClaim().getId());
			if (trackingLog.getResolutionPercentage() != null && trackingLogs.isPresent() && trackingLogs.get().size() > 0) {
				highestTrackingLog = trackingLogs.get().get(0);
				long completedTrackingLogs = trackingLogs.get().stream().filter(t -> t.getResolutionPercentage() == 100).count();
				if (highestTrackingLog.getId() != trackingLog.getId())
					if (highestTrackingLog.getResolutionPercentage() == 100 && trackingLog.getResolutionPercentage() == 100) {
						valid = !highestTrackingLog.isDraftMode() && completedTrackingLogs < 2;
						super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.maxcompleted");
					} else {
						valid = highestTrackingLog.getResolutionPercentage() < trackingLog.getResolutionPercentage();
						super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.badPercentage");
					}
			}
		}

	}

	@Override
	public void perform(final TrackingLog trackingLog) {

		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

		SelectChoices statusChoices;
		Dataset dataset;

		statusChoices = SelectChoices.from(ClaimStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		dataset.put("statusChoices", statusChoices);

		int claimId = trackingLog.getClaim() != null ? trackingLog.getClaim().getId() : 0;
		dataset.put("claimId", claimId);

		super.getResponse().addData(dataset);

		/*
		 * SelectChoices statusChoices;
		 * 
		 * Dataset dataset;
		 * statusChoices = SelectChoices.from(ClaimStatus.class, trackingLog.getStatus());
		 * 
		 * dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		 * dataset.put("statusChoices", statusChoices);
		 * 
		 * int claimId = super.getRequest().getData("claimId", int.class);
		 * dataset.put("claimId", claimId);
		 * 
		 * super.getResponse().addData(dataset);
		 */
	}

}
