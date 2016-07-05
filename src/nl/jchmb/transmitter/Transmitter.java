package nl.jchmb.transmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Transmitter implements AutoCloseable {
	private Thread senderThread;
	private Thread receiverThread;
	
	private long delay = 5L;
	
	private int count;
	private Map<Class<? extends Transmittable>, Integer> ids;
	private Map<Integer, Class<? extends Transmittable>> classes;
	private Map<Class<? extends Transmittable>, TransmissionListener<?>> listeners;
	
	private boolean running = false;
	
	private final Queue<Transmittable> received;
	private final Queue<Transmittable> sent;
	
	protected Transmitter() {
		ids = new HashMap<>();
		classes = new HashMap<>();
		listeners = new HashMap<>();
		count = 0;
		
		received = new ConcurrentLinkedQueue<>();
		sent = new ConcurrentLinkedQueue<>();
	}
	
	public <T extends Transmittable> void addListener(Class<T> cls, TransmissionListener<T> listener) {
		listeners.put(cls, listener);
	}
	
	public void register(Class<? extends Transmittable> cls) {
		int id = ++count;
		ids.put(cls, id);
		classes.put(id, cls);
	}
	
	public int getID(Class<? extends Transmittable> cls) {
		return ids.containsKey(cls) ?
				ids.get(cls) : -1;
	}
	
	public Class<? extends Transmittable> get(int id) {
		return classes.get(id);
	}
	
	public abstract void connect();
	
	public abstract boolean isConnected();
	protected abstract Transmittable receive() throws TransmitterException;
	
	public void send(Transmittable transmission) {
		sent.offer(transmission);
	}
	
	protected abstract void executeSending(Transmittable transmittable) throws TransmitterException;
	
	protected <T extends Transmittable> void onReceive(T transmission) {
		@SuppressWarnings("unchecked")
		TransmissionListener<T> listener = 
			(TransmissionListener<T>) listeners.get(transmission.getClass());
		listener.onReceive(transmission);
	}
	
	private void executeReceiving() {
		Transmittable transmission = null;
		try {
			transmission = receive();
		} catch (TransmitterException e) {
			e.printStackTrace();
			running = false;
			return;
		}
		if (transmission != null) {
			received.offer(transmission);
		}
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
			running = false;
		}
	}
	
	private void clearBuffers() {
		received.clear();
		sent.clear();
	}
	
	public void start() {
		running = true;
		clearBuffers();
		connect();
		senderThread = new Thread(new SenderTask());
		senderThread.start();
		receiverThread = new Thread(new ReceiverTask());
		receiverThread.start();
	}
	
	public void stop() {
		running = false;
		close();
		try {
			senderThread.join();
			receiverThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void close();
	
	private class SenderTask implements Runnable {

		@Override
		public void run() {
			while (running) {
				try {
					Transmittable transmission = sent.poll();
					if (transmission != null) {
						executeSending(transmission);
					}
					Thread.sleep(delay);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class ReceiverTask implements Runnable {

		@Override
		public void run() {
			while (running) {
				try {
					executeReceiving();
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
