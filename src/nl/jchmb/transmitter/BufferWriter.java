package nl.jchmb.transmitter;

import java.nio.ByteBuffer;

public interface BufferWriter<T> {
	public void write(ByteBuffer buffer, T value) throws TransmitterException;
}
