package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.GetTickEvent;
import bgu.spl.mics.application.messages.StopTickBroadcast;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import javax.swing.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
    Timer timer;
    int speed;
    int duration;
    int tick = 0;

    /**
     * TimeService's constructor.
     *
     * @param speed - the speed of the delivery vehicle.
     * @param duration - the time it takes to get to the customer from the store.
     */
    public TimeService(Integer speed, Integer duration) {
        super("Time Service");
        this.speed = speed;
        this.duration = duration;
    }

    /**
     * This method initializes the TimeService.
     */
    protected void initialize() {
        sendTick();
        terminateService();
        completeEvent();
        this.timer.start();
    }

    /**
     * This method makes sure that TimeService responds to tickEvent.
     */
    private void sendTick(){
        timer = new Timer(this.speed, tickEvent->{
            tick++;
            if (this.tick == this.duration) {
                sendBroadcast(new StopTickBroadcast(tick));
                this.timer.stop();
            }
            else
                sendBroadcast(new TickBroadcast(tick));
        });
    }

    /**
     * This method makes sure that the TimeService terminates itself
     * when StopTickBroadcast is received.
     */
    private void terminateService(){
        this.subscribeBroadcast(StopTickBroadcast.class, terminateTick->{
            this.terminate();
        });
    }

    /**
     * This method makes sure that TimeService responds to GetTickEvent.
     */
    private void completeEvent(){
        this.subscribeEvent(GetTickEvent.class, e->{
            complete(e, this.tick); // returns the current tick
        });
    }
}
