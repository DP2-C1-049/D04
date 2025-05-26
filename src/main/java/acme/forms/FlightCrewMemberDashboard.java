
package acme.forms;

import java.util.Collection;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// 1. Últimos cinco destinos asignados
	Collection<String>			lastFiveDestinations;

	// 2. Número de tramos con incidentes por rango de severidad
	int							severityRange0to3;
	int							severityRange4to7;
	int							severityRange8to10;

	// 3. Miembros de la tripulación que compartieron el último tramo
	Collection<String>			crewMembersInLastLeg;

	// 4. Asignaciones agrupadas por estado
	Map<String, Integer>		assignmentsByStatus;

	// 5. Estadísticas de asignaciones en el último mes
	Double						assignmentAverageLastMonth;
	int							assignmentMinLastMonth;
	int							assignmentMaxLastMonth;
	Double						assignmentDeviationLastMonth;
}
