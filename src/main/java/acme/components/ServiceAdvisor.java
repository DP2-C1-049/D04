
package acme.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import acme.entities.service.Service;

@ControllerAdvice
public class ServiceAdvisor {

	@Autowired
	private ServiceRepository repository;


	@ModelAttribute("service")
	public Service getService() {
		Service result;

		try {
			Service service = this.repository.findRandomService();
			result = service != null ? service : null;
		} catch (final Throwable oops) {
			result = null;
		}

		return result;
	}

}
