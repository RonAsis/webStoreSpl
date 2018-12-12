package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private ConcurrentHashMap<DeliveryVehicle,Boolean> mapDeliveryVehicle;
	private int  catchingVehicle;
	private int amountVehicle;
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourcesHolder.SingletonHolder.instance;
	}
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	private ResourcesHolder() {
		mapDeliveryVehicle=new ConcurrentHashMap<>();
		catchingVehicle=0;
		amountVehicle=0;
	}
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public synchronized  Future<DeliveryVehicle> acquireVehicle() {
			while (!haveFreeVehicle()) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			Future<DeliveryVehicle> future=new Future();
			future.resolve(freeVehicle());
			notifyAll();
			return future;
		}

	private DeliveryVehicle freeVehicle() {
		for (DeliveryVehicle key : mapDeliveryVehicle.keySet()) {
			if (mapDeliveryVehicle.get(key) == false) {
				mapDeliveryVehicle.put(key, true);
				return key;
			}
		}
		return null;
	}
	private boolean haveFreeVehicle() {
			return this.amountVehicle - this.catchingVehicle > 0;
		}
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		this.mapDeliveryVehicle.put(vehicle,false);
	}
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i=0;i<vehicles.length;i++)
			mapDeliveryVehicle.putIfAbsent(vehicles[i],false);
		this.amountVehicle=vehicles.length;
	}

}
