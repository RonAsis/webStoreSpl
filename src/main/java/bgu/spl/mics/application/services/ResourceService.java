package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ResourceEvent;
import bgu.spl.mics.application.messages.StopTickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

import java.util.concurrent.TimeUnit;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder;

	/**
	 * ResourceService's constructor.
	 *
	 * @param name - the name of the ResourceService.
	 */
	public ResourceService(String name) {
		super(name);
		this.resourcesHolder = ResourcesHolder.getInstance();
	}

	/**
	 * This method initializes the ResourceService.
	 */
	protected void initialize() {
		terminateService();
		sendVehicle();
	}

	/**
	 * This method makes sure that the ResourceService terminates itself
	 * when StopTickBroadcast is received.
	 */
	private void terminateService(){
		this.subscribeBroadcast(StopTickBroadcast.class, terminateTick->{
			this.terminate();
		});
	}


	/**
	 * This method makes sure that ResourceService responds to to a given ResourceEvent.
	 */
	private void sendVehicle(){
		this.subscribeEvent(ResourceEvent.class, deliveryMessage-> {
			Future<DeliveryVehicle> futureDeliveryVehicle =	this.resourcesHolder.acquireVehicle();

			if (futureDeliveryVehicle!=null){
				DeliveryVehicle deliveryVehicle = futureDeliveryVehicle.get(1, TimeUnit.SECONDS);

				if (deliveryVehicle!=null){
					deliveryVehicle.deliver(deliveryMessage.getDeliveryMessage().getAddress(), deliveryMessage.getDeliveryMessage().getDistance());
					this.resourcesHolder.releaseVehicle(deliveryVehicle);
				}
			}
		});
	}

}
