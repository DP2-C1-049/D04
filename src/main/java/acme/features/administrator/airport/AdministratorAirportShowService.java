
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;

@GuiService
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		try {
			if (!super.getRequest().getMethod().equals("GET"))
				super.getResponse().setAuthorised(false);
			else {
				Integer id = super.getRequest().getData("id", Integer.class);
				if (id == null)
					super.getResponse().setAuthorised(false);
				else {
					Airport airport = this.repository.findAirportById(id);
					super.getResponse().setAuthorised(airport != null);
				}
			}
		} catch (Throwable t) {
			super.getResponse().setAuthorised(false);
		}
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
	public void unbind(final Airport airport) {
		Dataset dataset;

		dataset = super.unbindObject(airport, "name", "iataCode", "scope", "city", "country", "website", "email", "phoneNumber");

		super.getResponse().addData(dataset);
	}
}
