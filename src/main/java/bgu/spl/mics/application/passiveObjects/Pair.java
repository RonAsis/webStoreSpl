package bgu.spl.mics.application.passiveObjects;

/**
 * This class represents an order a customer needs to make,
 * each order has the name of the book the needs to be ordered
 * and the tick of which it needs to be ordered at.
 */
public class Pair {
    String name; //  the name of the book the needs to be ordered
    int tick; // the tick of which the book needs to be ordered at

    /**
     * Pair's constructor.
     *
     * @param name - the name of the book the needs to be ordered.
     * @param tick - the tick of which the book needs to be ordered at.
     */
    public Pair(String name, Integer tick){
        this.name = name;
        this.tick = tick;
    }

    /**
     * Getters.
     */
    public int getTick() {
        return this.tick;
    }

    public String getName() {
        return this.name;
    }
}
