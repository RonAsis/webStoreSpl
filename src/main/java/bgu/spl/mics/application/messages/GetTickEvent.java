package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

/**
 * An event that is sent when a service wishes to get the current time.
 * all of the services can send this event, TimeService responds.
 */
public class GetTickEvent implements Event {
    private int tick; // the current tick.

    /**
     * GetTickEvent's first constructor.
     *
     * @param tick - the current tick.
     */
    public GetTickEvent(int tick){
        this.tick = tick;
    }

    /**
     * GetTickEvent's second constructor.
     */
    public GetTickEvent(){}
}