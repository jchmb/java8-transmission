package nl.jchmb.transmitter;

import java.nio.ByteBuffer;

public interface BufferReader<T> {
	public T read(ByteBuffer buffer) throws TransmitterException;
}
