package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
public class DeliveryEvent  implements Event<String> {
    private String senderName;

    public DeliveryEvent(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}
