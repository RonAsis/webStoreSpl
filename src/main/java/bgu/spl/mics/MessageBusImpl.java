package bgu.spl.mics;

import java.util.Hashtable;
import java.util.Vector;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private class RoundrobinPattern{
	    private int count;
	    private Vector<MicroService > fVectorEvents;
	    private RoundrobinPattern(){
            count=0;
            fVectorEvents=new  Vector<MicroService >();
        }
        private MicroService getNext(){
	        int size =fVectorEvents.size();
	        MicroService ans= fVectorEvents.get(count-1);
            count=(count+1)%size;
            return ans;
        }
        private void addToVector(MicroService m){
            fVectorEvents.add(m);
        }
        private void removeFromVector (MicroService m ){
	        count--;
            fVectorEvents.remove(m);
        }
        private boolean contain (MicroService m){
	        return fVectorEvents.contains(m);
        }
    }
    private Hashtable<MicroService, Vector<Message>> fQueuesMicroService;// can find in this field all the queues of the micro-service according to the name of them
    private Hashtable<Class<? extends Event>,RoundrobinPattern> fMessagesEvent;
    private Hashtable<Class<? extends Broadcast>,Vector< MicroService >> fMessageBroadcast;
    private Hashtable<Class<? extends Event>,Future> fFutureOfEvents;

    /**for Safe Singleton of this class**/
	private static class SingletonHolder {
			private static MessageBusImpl instance = new MessageBusImpl();
		}
		private MessageBusImpl() {
            fQueuesMicroService=new Hashtable<MicroService, Vector<Message>>();
            fMessagesEvent=new Hashtable<Class<? extends Event>,RoundrobinPattern>();
            fMessageBroadcast=new Hashtable<Class<? extends Broadcast>,Vector< MicroService >>();
		}
		public static MessageBusImpl getInstance() {
			return SingletonHolder.instance;
		}

    /**
     * A Micro-Service calls this method in order to subscribe itself for some type of event(t he specific class type of the event is passed as
     * a parameter)
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     * @param <T>
     */
    //@PRE
	@Override
    //@PRE
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
        subscribeToVectorEvent(isEventExist(type),m);
	}
    private RoundrobinPattern isEventExist(Class<? extends Event> type){
        for(Class<? extends Event> key: fMessagesEvent.keySet()) {//////****//////
            if (key==type)//////****//////
                return fMessagesEvent.get(key);//////****//////
        }
        return null;
    }
    private void subscribeToVectorEvent( RoundrobinPattern microServices,MicroService m){
        if (microServices!=null && !microServices.contain(m))
            if(m!=null)
               microServices.addToVector(m);
    }
    /**
     * A Micro-Service calls this method in order to subscribe itself for some type of broadcast Message(t he specific class type of the event is passed as
     * a parameter)
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
     */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
        subscribeToVectorBroadcast(isBroadcastExist(type),m);
    }
    private void subscribeToVectorBroadcast( Vector<MicroService > microServices,MicroService m){
        if (microServices!=null && !microServices.contains(m))
            if(m!=null)
                microServices.add(m);
    }
    private Vector<MicroService > isBroadcastExist(Class<? extends Broadcast> type){
        for(Class<? extends Broadcast> key: fMessageBroadcast.keySet()) {//////****//////
            if (key==type)//////****//////
                return fMessageBroadcast.get(key);//////****//////
        }
        return null;
    }

    /**
     * Micro-Service calls this method in order to notify the Message-Bus that the event was handled, and providing the result of handling the request. The Future object associated with event e
     * should be resolved to the result given as a parameter
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @param <T>
     */
	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
        fFutureOfEvents.get(e).resolve(result);

	}

    /**
     * A Micro-Service calls this method in order to add a broadcast message to the queues of all Micro-Services which subscribed to receive this specific message type
     * @param b 	The message to added to the queues.
     */
	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
        addbroadcastMessageToTheQueues(getVectorSubscribeBroadcast(b),b );
	}
    private Vector<MicroService > getVectorSubscribeBroadcast(Broadcast b) {
	    for (Class<? extends Broadcast> key: fMessageBroadcast.keySet())
             if (key.equals(b))//////****//////
              return fMessageBroadcast.get(key);//////****//////
        return null;
    }
	private void addbroadcastMessageToTheQueues(Vector<MicroService > microService,Broadcast b ) {
        if (microService != null) {
            for (MicroService key : microService)
                fQueuesMicroService.get(key).add(b);
        }
    }

    /**
     * A Micro-Service calls this method in order to add the event e to the message queue of one of the Micro-Services
     * which have subscribed to receive events of type e.getClass().
     * The messages are added in a round-robin fashion. This method returns a Future object-from this object the sending Micro-Service can retrieves
     * the result of processing the event once it is completed.If the re is no suitable Micro-Service,should return null.
     * @param e     	The event to add to the queue.
     * @param <T>
     * @return
     */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
        if(subScribEventToQuene(e)){
            createFutrueEvent( e);
            return fFutureOfEvents.get(e.getClass());
        }
        return null;
	}
    private <T> boolean  subScribEventToQuene(Event<T> e){
        MicroService ms= fMessagesEvent.get(e).getNext();
        if(ms!=null) {
            fQueuesMicroService.get(ms).add(e);
            return true;
        }
        return false;
    }
    private <T> void createFutrueEvent(Event<T> e){
        fFutureOfEvents.put(e.getClass(),new Future());
    }

    /**
     * a Micro-Service calls this method in order to register itself. This method should create a queue for the Micro-Service in the Message-Bus
     * @param m the micro-service to create a queue for.
     */
	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub
        if (isRegister(m)==null)
            addQueueMicroService(m);
	}
    private Vector<Message> isRegister(MicroService m){
        for(MicroService key: fQueuesMicroService.keySet()) {//////****//////
            if (key==m)//////****//////
                return fQueuesMicroService.get(key);//////****//////
        }
        return null;
    }
    private void addQueueMicroService(MicroService m){
        fQueuesMicroService.put(m,new  Vector< Message>());
    }

    /**
     * A Micro-Service calls this method in order to unregister itself.Should remove the message queue allocated to the Micro-Service and clean all the references related to this Message-Bus
     * @param m the micro-service to unregister.
     */
	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
        fQueuesMicroService.remove(m);
        fMessagesEvent.remove(m);
        fMessageBroadcast.remove(m);
	}

    /**
     * A Micro-Service calls this method in order to take a message from its allocated queue. This method is blocking
     * (waits until there is an available message and returns it).
     * @param m The micro-service requesting to take a message from its message queue.
     * @return
     * @throws InterruptedException
     */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
       // blockingAwaitMessage(m);
        return fQueuesMicroService.get(m).get(0);
	}
	private void blockingAwaitMessage(MicroService m){
        while(fQueuesMicroService.get(m).isEmpty()){}
    }


	

}
