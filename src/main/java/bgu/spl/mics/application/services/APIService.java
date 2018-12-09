package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;

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
	Customer customer;
	List<Pair> orderSchedule;

	public APIService(Customer customer, List<Pair> orderSchedule) {
		super(customer.getName());
		this.customer = customer;
		this.orderSchedule = orderSchedule;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, orderBook-> {
			for (Pair pair : orderSchedule) {
				if (pair.getTick() == orderBook.getTick()){
					Future<OrderResult> futureOrder = sendEvent(new BookOrderEvent(pair.getName()));
				}
			}
		});
	}

}
