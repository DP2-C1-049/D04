
package acme.features.customer.bookingRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.booking.BookingRecord;
import acme.realms.Customers;

@GuiController
public class CustomerBookingRecordController extends AbstractGuiController<Customers, BookingRecord> {
	// Internal state ---------------------------------------------------------

	//@Autowired
	//private CustomerBookingRecordListService	listService;

	//@Autowired
	//private CustomerBookingRecordShowService	showService;

	@Autowired
	private CustomerBookingRecordCreateService createService;

	//@Autowired
	//private CustomerBookingUpdateRecordService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		//super.addBasicCommand("list", this.listService);
		//super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		//super.addBasicCommand("update", this.updateService);

	}

}
