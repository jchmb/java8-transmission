package nl.jchmb.transmitter.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import nl.jchmb.transmitter.BufferedTransmitter;

public class UDPTransmitter extends BufferedTransmitter {
	private DatagramChannel channel;
	private SocketAddress address;
	
	public UDPTransmitter(SocketAddress address) {
		super();
		this.address = address;
	}
	
	@Override
	protected void receiveBuffer(ByteBuffer buffer) {
		try {
			channel.receive(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void sendBuffer(ByteBuffer buffer) {
		try {
			channel.send(buffer, address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect() {
		try {
			channel = DatagramChannel.open();
			channel.connect(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
