package bgu.spl.mics.application.accessoores;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RunnerTherds {
    ConcurrentLinkedQueue< Thread > threads;
    public RunnerTherds(ConcurrentLinkedQueue< Thread > threads){
        this.threads=threads;

    }
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
