
package acme.features.assistanceAgents.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.Claim;
import acme.realms.AssistanceAgents;

@GuiController
public class ClaimController extends AbstractGuiController<AssistanceAgents, Claim> {

	@Autowired
	private ResolvedClaimListService	listResolvedService;

	@Autowired
	private PendingClaimListService		listPendingService;

	@Autowired
	private ClaimShowService			showService;

	@Autowired
	private ClaimCreateService			createService;

	@Autowired
	private ClaimUpdateService			updateService;

	@Autowired
	private ClaimDeleteService			deleteService;

	@Autowired
	private ClaimPublishService			publishService;


	// Constructors -----------------------------------------------------------
	@PostConstruct
	protected void initialise() {
		//super.addBasicCommand("list", this.listResolvedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		//super.addCustomCommand("pending", "list", this.listPendingService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addCustomCommand("list-resolved", "list", this.listResolvedService); //
		super.addCustomCommand("list-pending", "list", this.listPendingService); //
	}
}
