import bgu.spl.mics.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MessageBusImplTest {
    private MessageBusImpl fMessageBusImpl;
    private MicroService fMicroService;
    @Before
    public void setUp() throws Exception {
        try {
            fMessageBusImpl=MessageBusImpl.getInstance();
            String[] args={"5"};
            fMicroService=new ExampleBroadcastListenerService("test",args);
        }
        catch (Exception e){
            System.out.println("Can't create the objects for the test for 'MessageBuslmpl' .");
        }

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getInstance() {
        //check if the instance of fMessageBusImpl is like MessageBusImpl.getInstance(). this say us if its Singleton
        assertEquals("should same Object",fMessageBusImpl,MessageBusImpl.getInstance());
    }

    @Test
    public void subscribeEvent() {
        Event event=registerAndSubscribeEvent();

        //Check if can send event after do subscribeEvent
        assertNotNull(fMessageBusImpl.sendEvent(event));
    }

    @Test
    public void subscribeBroadcast() {
        this.testerBroadcast();
    }

    @Test
    public void complete() {
        Event event =registerAndSubscribeEvent();
        Future future=fMessageBusImpl.sendEvent(event);
        fMessageBusImpl.complete(event,"test");
        assertTrue(future.isDone());
    }

    @Test
    public void sendBroadcast() {
        testerBroadcast();
    }

    @Test
    public void sendEvent() {
        Event event=registerAndSubscribeEvent();
        assertNull(fMessageBusImpl.sendEvent(event));
    }

    @Test
    public void register() {
        Event event= this.registerAndSubscribeEvent();

        // send event so we can check if this is registered need return null if not successful
        assertNotNull(fMessageBusImpl.sendEvent(event));
    }

    @Test
    public void unregister() {
        Event event=registerAndSubscribeEvent();

        // unregisted the micro server
        fMessageBusImpl.unregister(fMicroService);

        //check if this not register
        assertNull(fMessageBusImpl.sendEvent(event));
    }

    @Test
    public void awaitMessage() {
        Event event =registerAndSubscribeEvent();

        //sent to awaitMessage the method won't waiting for an event to appear
        fMessageBusImpl.sendEvent(event);
        boolean subscribeBroadcast = true;
        try {
            fMessageBusImpl.awaitMessage(fMicroService);
        }
        catch (InterruptedException e){
            subscribeBroadcast = false;
        }
        assertTrue(subscribeBroadcast);
    }
    private Event registerAndSubscribeEvent(){
        //** register and subscribe the micro server
        ExampleEvent event= new ExampleEvent("testEvent");
        fMessageBusImpl.register(fMicroService);
        fMessageBusImpl.subscribeEvent(event.getClass(),fMicroService);
        return event;
    }
    private Broadcast registerAndSubscribeBroadcast(){
        //** register and subscribe the micro server
        ExampleBroadcast broadcast= new ExampleBroadcast("testEvent");
        fMessageBusImpl.register(fMicroService);
        fMessageBusImpl.subscribeBroadcast(broadcast.getClass(),fMicroService);
        return broadcast;
    }
    private void testerBroadcast(){
        Broadcast broadcast =registerAndSubscribeBroadcast();

        //sent to awaitMessage the method won't waiting for an event to appear
        fMessageBusImpl.sendBroadcast(broadcast);
        boolean subscribeBroadcast = true;
        try {
            fMessageBusImpl.awaitMessage(fMicroService);
        }
        catch (InterruptedException e){
           subscribeBroadcast = false;
        }
        assertTrue(subscribeBroadcast);
    }

}