package nl.jchmb.transmitter;

import java.nio.ByteBuffer;

public class TransmissionIO {
	public static void writeInteger(ByteBuffer buffer, int value) throws TransmitterException {
		buffer.putInt(value);
	}
	
	public static int readInt(ByteBuffer buffer) throws TransmitterException {
		if (buffer.remaining() < Integer.BYTES) {
			throw new TransmitterException("Not enough bytes to read an integer.");
		}
		return buffer.getInt();
	}
}
