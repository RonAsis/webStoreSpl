package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

/**
 * A broadcast that is sent when TimeService's tick reaches its duration.
 * TimeService sends this broadcast, all of the services that subscribed to this event react.
 */
public class StopTickBroadcast implements Broadcast {
    private int tick;

    /**
     * StopTickBroadcast's constructor.
     *
     * @param tick - the timers' last tick.
     */
    public StopTickBroadcast(int tick){
        this.tick = tick;
    }

}
