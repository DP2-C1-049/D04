
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findById(flightId);
		boolean status = flight != null && flight.getManager().getId() == super.getRequest().getPrincipal().getActiveRealm().getId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indication", "cost", "description", "draftMode");
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
		dataset.put("draftMode", flight.isDraftMode());
		super.getResponse().addData(dataset);
	}
}
