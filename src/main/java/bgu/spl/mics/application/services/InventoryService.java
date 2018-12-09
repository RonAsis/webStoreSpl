package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Inventory;
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
	private Inventory inventory;

	public InventoryService() {
		super("Inventory Service");
		this.inventory=Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println("Service " + getName() + " started");
		subscribeBroadcast(ExampleBroadcast.class, message -> {
			mbt--;
			System.out.println("Service " + getName() + " got a new message from " + message.getSenderId() + "! (mbt: " + mbt + ")");
			if (mbt == 0) {
				System.out.println("Service " + getName() + " terminating.");
				terminate();
			}
		});
		
	}

}
