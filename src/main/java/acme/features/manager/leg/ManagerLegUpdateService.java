
package acme.features.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airport.Airport;
import acme.entities.leg.Leg;
import acme.entities.leg.Status;
import acme.features.administrator.aircraft.AdministratorAircraftRepository;
import acme.features.administrator.airport.AdministratorAirportRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

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
		boolean status = true;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int legId = super.getRequest().getData("id", int.class);
			Leg leg = this.repository.findOneLegByIdAndManager(legId, super.getRequest().getPrincipal().getActiveRealm().getId());
			status = leg != null && leg.isDraftMode();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findOneLegByIdAndManager(legId, super.getRequest().getPrincipal().getActiveRealm().getId());
		super.getBuffer().addData(leg);

		Collection<Airport> airports = this.airportRepository.getAll();
		SelectChoices departureChoices = new SelectChoices();
		SelectChoices arrivalChoices = new SelectChoices();
		departureChoices.add("0", "----", leg.getDepartureAirport() == null);
		arrivalChoices.add("0", "----", leg.getArrivalAirport() == null);
		for (Airport ap : airports) {
			String iata = ap.getIataCode();
			departureChoices.add(iata, iata, leg.getDepartureAirport() != null && iata.equals(leg.getDepartureAirport().getIataCode()));
			arrivalChoices.add(iata, iata, leg.getArrivalAirport() != null && iata.equals(leg.getArrivalAirport().getIataCode()));
		}
		super.getResponse().addGlobal("departureAirports", departureChoices);
		super.getResponse().addGlobal("arrivalAirports", arrivalChoices);

		Collection<Aircraft> aircrafts = this.aircraftRepository.findAllAircrafts().stream().filter(a -> !a.isDisabled()).collect(Collectors.toCollection(ArrayList::new));
		SelectChoices aircraftChoices = new SelectChoices();
		aircraftChoices.add("0", "----", leg.getAircraft() == null);
		for (Aircraft ac : aircrafts) {
			String key = String.valueOf(ac.getId());
			aircraftChoices.add(key, ac.getRegistrationNumber(), leg.getAircraft() != null && key.equals(String.valueOf(leg.getAircraft().getId())));
		}
		super.getResponse().addGlobal("aircraftChoices", aircraftChoices);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "departure", "arrival", "status");
		String statusStr = super.getRequest().getData("status", String.class);
		if (statusStr != null && !statusStr.isEmpty())
			try {
				Status newStatus = Status.valueOf(statusStr);
				leg.setStatus(newStatus);
			} catch (IllegalArgumentException ex) {
			}
		String departureIata = super.getRequest().getData("departureAirport", String.class);
		if ("0".equals(departureIata))
			leg.setDepartureAirport(null);
		else {
			Airport departureAirport = this.airportRepository.findAirportByIataCode(departureIata);
			if (departureAirport == null)
				throw new IllegalStateException("Access not authorised");
			leg.setDepartureAirport(departureAirport);
		}
		String arrivalIata = super.getRequest().getData("arrivalAirport", String.class);
		if ("0".equals(arrivalIata))
			leg.setArrivalAirport(null);
		else {
			Airport arrivalAirport = this.airportRepository.findAirportByIataCode(arrivalIata);
			if (arrivalAirport == null)
				throw new IllegalStateException("Access not authorised");
			leg.setArrivalAirport(arrivalAirport);
		}
		Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
		if (aircraftId == null || aircraftId == 0)
			leg.setAircraft(null);
		else {
			Aircraft aircraft = this.aircraftRepository.findAircraftById(aircraftId);
			if (aircraft == null)
				throw new IllegalStateException("Access not authorised");
			leg.setAircraft(aircraft);
		}

	}

	@Override
	public void validate(final Leg leg) {

		if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
			boolean valid = !(leg.getDepartureAirport().getId() == leg.getArrivalAirport().getId());
			super.state(valid, "arrivalAirport", "manager.leg.error.sameAirport");
			Leg existing = this.repository.findLegByFlightNumber(leg.getFlightNumber());
			boolean validFlightNumber = existing == null || existing.getId() == leg.getId();
			super.state(validFlightNumber, "flightNumber", "manager.leg.error.duplicateFlightNumber");
			super.state(leg.getDeparture() != null, "departure", "manager.leg.error.required.date");
			super.state(leg.getArrival() != null, "arrival", "manager.leg.error.required.date");
			if (leg.getDeparture() != null && leg.getArrival() != null) {
				super.state(leg.getDeparture().before(leg.getArrival()), "departure", "manager.leg.error.departureBeforeArrival");
				if (!leg.getDeparture().before(MomentHelper.getCurrentMoment()) && !leg.getArrival().before(MomentHelper.getCurrentMoment()))
					super.state(leg.getStatus().equals(Status.ON_TIME) || leg.getStatus().equals(Status.CANCELLED) || leg.getStatus().equals(Status.DELAYED), "status", "manager.leg.error.wrongFutureStatus");
				if (leg.getDeparture().before(MomentHelper.getCurrentMoment()) && !leg.getArrival().before(MomentHelper.getCurrentMoment()))
					super.state(leg.getStatus().equals(Status.ON_TIME) || leg.getStatus().equals(Status.CANCELLED) || leg.getStatus().equals(Status.DELAYED), "status", "manager.leg.error.wrongPresentStatus");
				if (leg.getDeparture().before(MomentHelper.getCurrentMoment()) && leg.getArrival().before(MomentHelper.getCurrentMoment()))
					super.state(leg.getStatus().equals(Status.LANDED) || leg.getStatus().equals(Status.CANCELLED), "status", "manager.leg.error.wrongPastStatus");
			}
			super.state(leg.getFlightNumber().contains(leg.getAircraft().getAirline().getIATACode()), "flightNumber", "manager.leg.error.wrongFlightNumber");
			super.state(!leg.getAircraft().isDisabled() && leg.getAircraft().getStatus().equals(AircraftStatus.ACTIVE), "aircraft", "manager.leg.error.aircraftDisabled");
		}
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset ds = super.unbindObject(leg, "flightNumber", "departure", "arrival", "draftMode");
		if (leg.getDepartureAirport() != null) {
			ds.put("departureAirport", leg.getDepartureAirport().getIataCode());
			ds.put("originCity", leg.getDepartureAirport().getCity());
		}
		if (leg.getArrivalAirport() != null) {
			ds.put("arrivalAirport", leg.getArrivalAirport().getIataCode());
			ds.put("destinationCity", leg.getArrivalAirport().getCity());
		}

		if (leg.getAircraft() != null) {
			ds.put("aircraft", leg.getAircraft().getId());
			ds.put("aircraftRegistration", leg.getAircraft().getRegistrationNumber());
		}

		ds.put("departure", new Object[] {
			leg.getDeparture()
		});
		ds.put("arrival", new Object[] {
			leg.getArrival()
		});

		ds.put("status", leg.getStatus());
		ds.put("legStatuses", SelectChoices.from(Status.class, leg.getStatus()));
		ds.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(ds);
	}
}
