package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;

import java.util.Comparator;
import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	//@INV for (i:0->customer.getCustomerReceiptList.size()) customer.getCustomerReceiptList.get(i).issuedTick>=
	// 									orderSchedule.get(customer.getCustomerReceiptList.get(i).getName).getTick()
	Customer customer; // the customer that is represented by this this APIService
	List<Pair> orderSchedule; // a list of the orders service needs to make

	/**
	 * APIService's constructor.
	 *
	 * @param customerId - the id that represents the customer, represents the APIService.
	 * @param customer - the customer that is represented by this this APIService.
	 * @param orderSchedule - a list of the orders service needs to make.
	 */
	public APIService(String customerId, Customer customer, List<Pair> orderSchedule) {
		super(customerId);
		this.customer = customer;
		this.orderSchedule = orderSchedule;
		this.orderSchedule.sort(Comparator.comparing(Pair::getTick)); // sorting the given list of orders, by tick
	}

	/**
	 * This method initializes the APIService.
	 */
	protected void initialize() {
		placeOrder();
	}

	/**
	 * This method places all of the orders that need to be
	 * made when TickBroadcast is received, if the received tick isn't the last one.
	 * If the received tick is the last tick, APIService terminates itself.
	 *
	 * The orders that need to be made, are those where the pair's tick (/the order's tick)
	 * is equal to the given tick from the broadcast.
	 */
	private void placeOrder(){
		this.subscribeBroadcast(TickBroadcast.class, orderBook-> {
			if (orderBook.getLastTick() == false)
				for (Pair pair : orderSchedule) {
					if (pair.getTick() == orderBook.getTick()){
						Future<OrderReceipt> futureOrder = sendEvent(new BookOrderEvent(pair.getName(), this.customer, pair.getTick())); // ordering the book

						if (futureOrder!=null) {
							OrderReceipt receipt = futureOrder.get();

							if (receipt != null) {
								this.customer.addReceipts(receipt);
							}
						}
					}
					else if (pair.getTick() > orderBook.getTick()){
						break; // since orderSchedule is sorted, once the pair's tick is greater than the given tick, there's no need to keep looking for orders
					}
				}
			else
				this.terminate();
		});
	}

}
