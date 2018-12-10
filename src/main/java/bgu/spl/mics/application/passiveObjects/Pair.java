package bgu.spl.mics.application.passiveObjects;

public class Pair {
    String name;
    int tick;

    public Pair(String name, Integer tick){
        this.name = name;
        this.tick = tick;
    }

    public int getTick() {
        return this.tick;
    }

    public String getName() {
        return this.name;
    }
}
