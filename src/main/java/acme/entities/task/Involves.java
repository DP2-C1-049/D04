
package acme.entities.task;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "maintenance_record_id"), @Index(columnList = "maintenance_record_id,task_id"), @Index(columnList = "task_id")

})
public class Involves extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne
	private Task				task;

	@Mandatory
	@Valid
	@ManyToOne
	private MaintenanceRecord	maintenanceRecord;
}
