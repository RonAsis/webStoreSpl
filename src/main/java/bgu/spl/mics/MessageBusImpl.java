package bgu.spl.mics;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> fQueuesMicroService;// can find in this field all the queues of the micro-service according to the name of them
    private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue< MicroService >> fMessage;
    private ConcurrentHashMap<Class<? extends Event>,Future> fFutureOfEvents;
    private ConcurrentHashMap<MicroService,ConcurrentLinkedQueue<Future>> fMicroServiceAnsFuture;
    private final Object lockFuture=new Object();// lock the two data base of future
    private final Object lockQueuesMicroService=new Object();
    private final Object lockMessageEvent=new Object();
    private final Object lockMessageBroadcast=new Object();
    /**for Safe Singleton of this class**/
	private static class SingletonHolder {
			private static MessageBusImpl instance = new MessageBusImpl();
		}
		private MessageBusImpl() {
            fQueuesMicroService= new ConcurrentHashMap<>();
            fMessage= new ConcurrentHashMap<>();
            fFutureOfEvents= new ConcurrentHashMap<>();
            fMicroServiceAnsFuture=new ConcurrentHashMap<>();
		}
		public static MessageBusImpl getInstance() {
			return SingletonHolder.instance;
		}

    /**
     * A Micro-Service calls this method in order to subscribe itself for some type of event(t he specific class type of the event is passed as
     * a parameter)
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     */
	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (lockMessageEvent) {
            fMessage.putIfAbsent(type, new ConcurrentLinkedQueue<>());
            subscribe(type,m);
        }
    }
	private void subscribe(Class<? extends Message> type, MicroService m){
        ConcurrentLinkedQueue<MicroService> queue = fMessage.get(type);
        queue.add(m);
        }
    /**
     * A Micro-Service calls this method in order to subscribe itself for some type of broadcast Message(t he specific class type of the event is passed as
     * a parameter)
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
     */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized(lockMessageBroadcast){
            fMessage.putIfAbsent(type,new ConcurrentLinkedQueue<  >());
            subscribe(type,m);
        }
    }

    /**
     * Micro-Service calls this method in order to notify the Message-Bus that the event was handled, and providing the result of handling the request. The Future object associated with event e
     * should be resolved to the result given as a parameter
     * @param e      The completed eve/nt.
     * @param result The resolved result of the completed event.
     */
	@Override
	public <T> void complete(Event<T> e, T result) {
        synchronized (lockFuture) {
            fFutureOfEvents.get(e).resolve(result);
        }
    }
    /**
     * A Micro-Service calls this method in order to add a broadcast message to the queues of all Micro-Services which subscribed to receive this specific message type
     * @param b 	The message to added to the queues.
     */
	@Override
	public void sendBroadcast(Broadcast b) {
	    if (System.identityHashCode(lockMessageBroadcast)<System.identityHashCode(fQueuesMicroService))
        synchronized(lockMessageBroadcast) {
            synchronized (lockQueuesMicroService) {
                doSendBroadcat(b);
            }
        }
        else
            synchronized(lockQueuesMicroService) {
                synchronized (lockMessageBroadcast) {
                    doSendBroadcat(b);
                }
            }
	}
    private void doSendBroadcat(Broadcast b) {
        ConcurrentLinkedQueue<MicroService> queue = fMessage.get(b.getClass());
            for (MicroService key : queue)
                fQueuesMicroService.get(key).add(b.getClass());
        }
    /**
     * A Micro-Service calls this method in order to add the event e to the message queue of one of the Micro-Services
     * which have subscribed to receive events of type e.getClass().
     * The messages are added in a round-robin fashion. This method returns a Future object-from this object the sending Micro-Service can retrieves
     * the result of processing the event once it is completed.If the re is no suitable Micro-Service,should return null.
     * @param e     	The event to add to the queue.
     */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
        if (System.identityHashCode(lockMessageEvent)<System.identityHashCode(lockQueuesMicroService))
            synchronized(lockMessageEvent) {
                synchronized (lockQueuesMicroService) {
                  return doSendEvent(e);
                }
            }
        else
            synchronized(lockQueuesMicroService) {
                synchronized (lockMessageEvent) {
                  return doSendEvent(e);
                }
            }
	}
	private <T> Future<T>  doSendEvent (Event<T> e){
        MicroService ms;
        synchronized(lockFuture) {
            if ((ms = subScribEventToQuene(e)) != null) {
                return createFutrueEvent(e, ms);
            }
            return null;
        }
    }
    private <T> MicroService  subScribEventToQuene(Event<T> e){
        MicroService ms= fMessage.get(e).poll();
        fMessage.get(e).add(ms);
        if(ms!=null) {
            fQueuesMicroService.get(ms).add(e.getClass());
            fMicroServiceAnsFuture.putIfAbsent(ms,new ConcurrentLinkedQueue<>());
            return ms;
        }
        return null;
    }
    private  <T> Future<T> createFutrueEvent(Event<T> e,MicroService ms){
	    Future <T> future=new Future<>();
        fMicroServiceAnsFuture.get(ms).add(future);
        fFutureOfEvents.put(e.getClass(),future);
        return future;
    }

    /**
     * a Micro-Service calls this method in order to register itself. This method should create a queue for the Micro-Service in the Message-Bus
     * @param m the micro-service to create a queue for.
     */
	@Override
	public void register(MicroService m) {
        fQueuesMicroService.putIfAbsent(m,new ConcurrentLinkedQueue<>());
	}


    /**
     * A Micro-Service calls this method in order to unregister itself.Should remove the message queue allocated to the Micro-Service and clean all the references related to this Message-Bus
     * @param m the micro-service to unregister.
     */
	@Override
	public synchronized void unregister(MicroService m) {
        ConcurrentLinkedQueue<Future> queueFuture=fMicroServiceAnsFuture.get(m);
	        for (Future key :queueFuture){
	            key.resolve(null);
            }
            fQueuesMicroService.remove(m);
        for (Class<?extends Message> key :fMessage.keySet()){
            fMessage.remove(key,m);
        }
	}

    /**
     * A Micro-Service calls this method in order to take a message from its allocated queue. This method is blocking
     * (waits until there is an available message and returns it).
     * @param m The micro-service requesting to take a message from its message queue.
     * @return
     */
	@Override
	public Message awaitMessage(MicroService m){
	    synchronized (lockQueuesMicroService){
	        while (fQueuesMicroService.get(m).isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            Message  message=fQueuesMicroService.get(m).poll().cast(Message.class);
	        notifyAll();
	        return message;
        }
	}
}
