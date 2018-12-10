package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class ResourceEvent implements Event {
    private DeliveryEvent deliveryMessage;

    public ResourceEvent(DeliveryEvent deliveryEvent){
        this.deliveryMessage = deliveryEvent;
    }

    public DeliveryEvent getDeliveryMessage() {
        return deliveryMessage;
    }
}
