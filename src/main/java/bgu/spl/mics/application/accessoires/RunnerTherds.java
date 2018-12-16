package bgu.spl.mics.application.accessoires;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RunnerTherds {
    ConcurrentLinkedQueue< Thread > threads;

    /**
     * runner all the threads
     * @param threads
     */
    public RunnerTherds(ConcurrentLinkedQueue< Thread > threads){
        this.threads=threads;

    }
/**
 * run the Threads : the timer is in the end
 */
    public   void runner(){
        for(Thread t:threads){
            t.start();
        }
        for(Thread t:threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
