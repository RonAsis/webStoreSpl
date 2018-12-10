package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GetTickEvent implements Event {
    private int tick;

    public GetTickEvent(int tick){
        this.tick = tick;
    }
    public GetTickEvent(){}
}