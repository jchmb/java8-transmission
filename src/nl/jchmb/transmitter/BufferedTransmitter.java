package nl.jchmb.transmitter;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;

public abstract class BufferedTransmitter extends Transmitter {
	private int bufferSize = 4096;
	private ByteBuffer sendingBuffer;
	private ByteBuffer receivingBuffer;
	
	protected BufferedTransmitter() {
		super();
		sendingBuffer = ByteBuffer.allocateDirect(bufferSize);
		receivingBuffer = ByteBuffer.allocateDirect(bufferSize);
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected abstract void receiveBuffer(ByteBuffer buffer);

	@SuppressWarnings("unchecked")
	@Override
	public Transmittable receive() throws TransmitterException {
		receivingBuffer = receivingBuffer;
		int index = receivingBuffer.getInt();
		Class<? extends Transmittable> cls = get(index);
		if (cls == null) {
			throw new TransmitterException("Invalid class referenced");
		}
		try {
			return cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new TransmitterException(e.getMessage());
		}
	}

	@Override
	protected void executeSending(Transmittable transmittable) throws TransmitterException {
		sendingBuffer.clear();
		TransmissionIO.writeInteger(sendingBuffer, getID(transmittable.getClass()));
		transmittable.write(sendingBuffer);
		sendingBuffer.flip();
		sendBuffer(sendingBuffer);
	}
	
	protected abstract void sendBuffer(ByteBuffer buffer);

}
