package nl.jchmb.transmitter;

import java.nio.ByteBuffer;

public interface Transmittable {
	public void write(ByteBuffer buffer) throws TransmitterException;
	public void read(ByteBuffer buffer) throws TransmitterException;
}