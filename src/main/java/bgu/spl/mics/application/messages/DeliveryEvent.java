package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * An event that is sent when a book needs to be delivered to a customer.
 * SellingService sends this event, LogisticsService responds.
 */
public class DeliveryEvent implements Event {
    private OrderReceipt orderReceipt; // the receipt of the order
    private String address; // the address the book needs to be delivered to
    private int distance; // the distance between the store and the customer's address

    /**
     * DeliveryEvent's constructor.
     *
     * @param orderReceipt - the receipt of the order.
     * @param address - the address the book needs to be delivered to.
     * @param distance - the distance between the store and the customer's address.
     */
    public DeliveryEvent(OrderReceipt orderReceipt, String address, int distance) {
        this.orderReceipt = orderReceipt;
        this.address = address;
        this.distance = distance;
    }

    /**
     * Getters.
     */
    public int getDistance() {
        return distance;
    }

    public OrderReceipt getOrderReceipt() {
        return orderReceipt;
    }

    public String getAddress() {
        return address;
    }
}
