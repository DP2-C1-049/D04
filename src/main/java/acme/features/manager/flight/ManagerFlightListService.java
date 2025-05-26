
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Principal manager = super.getRequest().getPrincipal();
		boolean isManager = manager.hasRealmOfType(Manager.class);
		super.getResponse().setAuthorised(isManager);
	}

	@Override
	public void load() {
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<Flight> flights = this.repository.findFlightsByManagerId(managerId);
		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description");

		if (flight.getDeparture() != null)
			dataset.put("departure", flight.getDeparture());
		if (flight.getArrival() != null)
			dataset.put("arrival", flight.getArrival());
		if (flight.getOriginCity() != null)
			dataset.put("originCity", flight.getOriginCity());
		else
			dataset.put("originCity", "");
		if (flight.getDestinationCity() != null)
			dataset.put("destinationCity", flight.getDestinationCity());
		else
			dataset.put("destinationCity", "");
		if (flight.getNumberOfLayovers() == -1)
			dataset.put("numberOfLayovers", 0);
		else
			dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		super.getResponse().addData(dataset);
	}
}
