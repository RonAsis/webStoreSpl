package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.TimeUnit;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private int orderId=1; // in order to keep truck of the receipts' ids
	private int tick;

	/**
	 * SellingService's constructor.
	 *
	 * @param name - the name of the sellingService.
	 */
	public SellingService(String name) {
		super(name);
		this.moneyRegister = MoneyRegister.getInstance();
	}

	/**
	 * This method initializes the sellingService.
	 */
	protected void initialize() {
		changeTick();
		orderBook();
	}

	/**
	 * This method makes sure that the sellingService changes its tick and terminates itself
	 * when the last tick is received.
	 */
	private void changeTick() {
		this.subscribeBroadcast(TickBroadcast.class, changeTick-> {
			this.tick = changeTick.getTick();
			if (changeTick.getLastTick() == true)
				this.terminate();
		});
	}

	/**
	 * This method makes sure that the sellingService responds to a given BookOrderEvent.
	 */
	private void orderBook() {
		this.subscribeEvent(BookOrderEvent.class, details -> {
			// Saving the price the customer paid for the book, this price is returned from TakeBookEvent
			Future<Integer> takeBook = sendEvent(new TakeBookEvent(details.getBookName(), details.getCustomer().getAvailableCreditAmount()));

			if (takeBook != null) {
				Integer price = takeBook.get(1, TimeUnit.SECONDS);

				// If the book was taking successfully,
				// i.e. the customer can afford the book and there's an available copy of the book
				if (price != null) {
					// Charging the customer for the book
					this.moneyRegister.chargeCreditCard(details.getCustomer(), price);

					// Initializing a new receipt for this order
					OrderReceipt receipt = new OrderReceipt();

					// Setting all of the receipt's details/information
					setReceipt(receipt, details, price);

					// Adding the receipt to list of receipts in moneyRegister
					moneyRegister.file(receipt);
					complete(details, receipt);

					// Ordering a delivery of the book
					sendEvent(new DeliveryEvent(receipt, details.getCustomer().getAddress(), details.getCustomer().getDistance()));
				}
				else
					complete(details, null);
			}
			else
				complete(details, null);
		});
	}

	/**
	 * This method setts all of the given receipt's details/information with the other parameters.
	 *
	 * @param receipt - the receipt that is being set.
	 * @param details - a BookOrderEvent.
	 * @param price - the price of the book that was bought.
	 */
	private void setReceipt(OrderReceipt receipt, BookOrderEvent details, int price){
		receipt.setBookTitle(details.getBookName());
		receipt.setCustomerId(details.getCustomer().getId());
		receipt.setOrderTick(details.getOrderTickTime());

		receipt.setPrice(price);

		receipt.setSeller(this.getName());
		receipt.setProcessTick(this.tick);
		receipt.setIssuedTick(this.tick);
		receipt.setOrderId(this.orderId);
		this.orderId++;
	}
}
