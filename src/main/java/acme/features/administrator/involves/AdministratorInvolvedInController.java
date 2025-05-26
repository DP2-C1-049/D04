
package acme.features.administrator.involves;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.task.Involves;

@GuiController
public class AdministratorInvolvedInController extends AbstractGuiController<Administrator, Involves> {

	@Autowired
	private AdministratorInvolvedInListService		listService;

	@Autowired
	private AdministratorInvolvedInShowTaskService	showTaskService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showTaskService);
	}
}
