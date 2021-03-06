package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ResourceEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;
/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	/**
	 * LogisticsService's constructor.
	 *
	 * @param name - the name of the LogisticsService.
	 */
	public LogisticsService(String name) {
		super(name);
	}

	/**
	 * This method initializes the LogisticsService.
	 */
	protected void initialize() {
		terminateService();
		sendDelivery();
	}

	/**
	 * This method makes sure that the LogisticsService terminates itself
	 * when the last tick is received.
	 */
	private void terminateService(){
		this.subscribeBroadcast(TickBroadcast.class, tickBroadcast->{
			if (tickBroadcast.getLastTick() == true)
				this.terminate();
		});
	}

	/**
	 * This method makes sure that LogisticsService responds to to a given DeliveryEvent.
	 */
	private void sendDelivery(){
		this.subscribeEvent(DeliveryEvent.class, deliveryMessage -> {
			ResourceEvent resourceMessage = new ResourceEvent(deliveryMessage);
			sendEvent(resourceMessage);
		});
	}
}
