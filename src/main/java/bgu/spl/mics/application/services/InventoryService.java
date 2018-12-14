package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.StopTickBroadcast;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	// @INV if (price=this.inventory.checkAvailabiltyAndGetPrice(details.getBookName()!=-1) price <=getMoneyLeft()
	private Inventory inventory;

	/**
	 * InventoryService's constructor.
	 *
	 * @param name - the name of the InventoryService
	 */
	public InventoryService(String name) {
		super(name);
		this.inventory=Inventory.getInstance();
	}

	/**
	 * This method initializes the sellingService.
	 */
	protected void initialize() {
		terminateService();
		takeBook();
	}

	/**
	 * This method makes sure that the InventoryService terminates itself
	 * when StopTickBroadcast is received.
	 */
	private void terminateService(){
		this.subscribeBroadcast(StopTickBroadcast.class, terminateTick->{
			this.terminate();
		});
	}

	/**
	 * This method makes sure that InventoryService responds to a given TakeBookEvent.
	 */
	private void takeBook(){
		this.subscribeEvent(TakeBookEvent.class, details -> {
			int price = this.inventory.checkAvailabiltyAndGetPrice(details.getBookName());

			if (price!=-1 && price <= details.getMoneyLeft()){
				OrderResult orderResult = this.inventory.take(details.getBookName());

				if (orderResult.equals(OrderResult.SUCCESSFULLY_TAKEN)){
					complete(details, price);
				}
				else // if the book is out of stock
					complete(details, null);
			}
			else { // if the customer can't afford the book or if the book is out of stock
				complete(details, null);
			}
		});
	}

}
