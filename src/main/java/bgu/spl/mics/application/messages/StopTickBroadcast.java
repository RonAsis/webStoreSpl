package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class StopTickBroadcast implements Broadcast {
    private int tick;

    public StopTickBroadcast(int tick){
        this.tick = tick;
    }

}
