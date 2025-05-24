
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int flightId = super.getRequest().getData("id", int.class);
			Flight flight = this.repository.findById(flightId);
			Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

			status = flight != null && flight.isDraftMode() && flight.getManager().getId() == manager.getId();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "indication", "cost", "description", "departure", "arrival");
	}

	@Override
	public void validate(final Flight flight) {
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
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
		Integer layovers = flight.getNumberOfLayovers();
		if (layovers == -1)
			dataset.put("numberOfLayovers", 0);
		super.getResponse().addData(dataset);
	}
}
