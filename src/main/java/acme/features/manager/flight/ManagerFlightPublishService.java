
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.leg.ManagerLegRepository;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository	repository;

	@Autowired
	private ManagerLegRepository	legRepository;

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
		Collection<Leg> legs = this.legRepository.findLegsByflightId(flight.getId());
		boolean hasLeg = !legs.isEmpty();
		super.state(hasLeg, "*", "flight.publish.error.noLegs");
		if (hasLeg) {
			boolean allPublished = legs.stream().allMatch(leg -> !leg.isDraftMode());
			super.state(allPublished, "*", "flight.publish.error.unpublishedLegs");
		}
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
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
		else
			dataset.put("numberOfLayovers", layovers != null ? layovers : 0);
		super.getResponse().addData(dataset);
	}
}
