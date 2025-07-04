
package acme.forms;

import acme.client.components.basis.AbstractForm;
import acme.entities.airport.Airport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Integer						rankingPosition;
	Integer						yearsRetire;
	Double						ratioOnTimeLegs;
	Double						ratioDelayedLegs;
	Airport						mostPopularAirportFlights;
	Airport						lessPopularAirportFlights;
	Integer						numberOfMyLegsOnTime;
	Integer						numberOfMyLegsDelayed;
	Integer						numberOfMyLegsCancelled;
	Integer						numberOfMyLegsLanded;
	Double						aveCost;
	Double						minCost;
	Double						maxCost;
	Double						standardDeviationCost;
}
