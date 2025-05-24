
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.Scope;

@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		if (!super.getRequest().getMethod().equals("POST"))
			super.getResponse().setAuthorised(false);
		else
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airport airport;
		int id;
		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);
		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "city", "country", "scope", "website", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		Airport existingAirport = this.repository.findAirportByIataCode(airport.getIataCode());
		boolean isValid = existingAirport == null || existingAirport.getId() == airport.getId();
		super.state(isValid, "iataCode", "administrator.airline.form.error.duplicateIata");
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices scopes = SelectChoices.from(Scope.class, airport.getScope());
		dataset = super.unbindObject(airport, "name", "iataCode", "city", "country", "scope", "website", "email", "phoneNumber");
		dataset.put("scopes", scopes);
		super.getResponse().addData(dataset);
	}
}
