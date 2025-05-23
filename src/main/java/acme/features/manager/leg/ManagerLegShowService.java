
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.leg.Leg;
import acme.entities.leg.Status;
import acme.features.administrator.aircraft.AdministratorAircraftRepository;
import acme.features.administrator.airport.AdministratorAirportRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository			repository;

	@Autowired
	private AdministratorAircraftRepository	aircraftRepository;

	@Autowired
	private AdministratorAirportRepository	airportRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean status = this.repository.findOneLegByIdAndManager(legId, managerId) != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);
		Collection<Airport> airports = this.airportRepository.getAll();
		SelectChoices departureChoices = new SelectChoices();
		if (leg.getDepartureAirport() == null)
			departureChoices.add("0", "----", true);
		else
			departureChoices.add("0", "----", false);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			boolean isSelected = leg.getDepartureAirport() != null && iata.equals(leg.getDepartureAirport().getIataCode());
			departureChoices.add(iata, iata, isSelected);
		}
		SelectChoices arrivalChoices = new SelectChoices();
		if (leg.getArrivalAirport() == null)
			arrivalChoices.add("0", "----", true);
		else
			arrivalChoices.add("0", "----", false);
		for (Airport airport : airports) {
			String iata = airport.getIataCode();
			boolean isSelected = leg.getArrivalAirport() != null && iata.equals(leg.getArrivalAirport().getIataCode());
			arrivalChoices.add(iata, iata, isSelected);
		}
		Collection<Aircraft> aircrafts = this.aircraftRepository.findAllAircrafts();
		SelectChoices aircraftChoices = new SelectChoices();
		if (leg.getAircraft() == null)
			aircraftChoices.add("0", "----", true);
		else
			aircraftChoices.add("0", "----", false);
		for (Aircraft ac : aircrafts) {
			String key = Integer.toString(ac.getId());
			String label = ac.getRegistrationNumber();
			boolean isSelected = leg.getAircraft() != null && key.equals(Integer.toString(leg.getAircraft().getId()));
			aircraftChoices.add(key, label, isSelected);
		}
		super.getResponse().addGlobal("departureAirports", departureChoices);
		super.getResponse().addGlobal("arrivalAirports", arrivalChoices);
		super.getResponse().addGlobal("aircraftChoices", aircraftChoices);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival", "draftMode");
		dataset.put("duration", leg.getDuration());
		if (leg.getDepartureAirport() != null) {
			dataset.put("departureAirport", leg.getDepartureAirport().getIataCode());
			dataset.put("originCity", leg.getDepartureAirport().getCity());
		}
		if (leg.getArrivalAirport() != null) {
			dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());
			dataset.put("destinationCity", leg.getArrivalAirport().getCity());
		}
		if (leg.getAircraft() != null) {
			dataset.put("aircraft", leg.getAircraft().getId());
			dataset.put("aircraftRegistration", leg.getAircraft().getRegistrationNumber());
		}
		dataset.put("departure", new Object[] {
			leg.getDeparture()
		});
		dataset.put("arrival", new Object[] {
			leg.getArrival()
		});
		dataset.put("status", leg.getStatus());
		SelectChoices choices = SelectChoices.from(Status.class, leg.getStatus());
		dataset.put("legStatuses", choices);
		dataset.put("flightId", leg.getFlight().getId());
		dataset.put("draftMode", leg.isDraftMode());
		super.getResponse().addData(dataset);
	}
}
