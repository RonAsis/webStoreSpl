package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.GetTickEvent;
import bgu.spl.mics.application.messages.StopTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

	public APIService(String customerId, Customer customer, List<Pair> orderSchedule) {
		super(customerId);
		this.customer = customer;
		this.orderSchedule = orderSchedule;
		this.orderSchedule.sort(Comparator.comparing(Pair::getTick));
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(StopTickBroadcast.class, terminateTick->{
			this.terminate();
		});

		subscribeBroadcast(TickBroadcast.class, orderBook-> {
			for (Pair pair : orderSchedule) {
				if (pair.getTick() == orderBook.getTick()){

					Future<Integer> orderTick = sendEvent(new GetTickEvent());
					Integer orderTickTime = orderTick.get(1, TimeUnit.MILLISECONDS);

					Future<OrderReceipt> futureOrder = sendEvent(new BookOrderEvent(pair.getName(), this.customer, orderTickTime));
					if (futureOrder.get(1, TimeUnit.MILLISECONDS)!=null) {
						OrderReceipt receipt = new OrderReceipt(futureOrder.get());
						this.customer.addReceipts(receipt);
					}
				}
				else if (pair.getTick() > orderBook.getTick()){
					break;
				}
			}
		});


	}
	/**
	 * This method makes sure that the APIService terminates itself
	 * when StopTickBroadcast is received.
	 */
	private void terminateService(){
		this.subscribeBroadcast(StopTickBroadcast.class, terminateTick->{
			this.terminate();
		});
	}
}
