package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

/**
 * A broadcast that is sent at every tick of the timer.
 * TimeService sends this broadcast, all of the services that subscribed to this event react.
 */
public class TickBroadcast implements Broadcast {
    private  int tick; // the current tick
    private boolean lastTick;
    /**
     * TickBroadcast's constructor.
     *
     * @param tick - the current tick.
     */
    public TickBroadcast(int tick, boolean lastTick) {
        this.tick = tick;
        this.lastTick = lastTick;
    }

    /**
     * Getter.
     */
    public int getTick() {
        return tick;
    }

    public boolean getLastTick(){ return this.lastTick; }
}
