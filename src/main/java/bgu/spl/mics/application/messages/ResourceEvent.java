package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

/**
 * An event that is sent when a book needs to be delivered to a customer.
 * LogisticsService sends this event, ResourceService responds.
 */
public class ResourceEvent implements Event {
    private DeliveryEvent deliveryMessage;

    /**
     * ResourceEvent's constructor.
     *
     * @param deliveryEvent - the details of the delivery.
     */
    public ResourceEvent(DeliveryEvent deliveryEvent){
        this.deliveryMessage = deliveryEvent;
    }

    /**
     * Getter.
     */
    public DeliveryEvent getDeliveryMessage() {
        return deliveryMessage;
    }
}
