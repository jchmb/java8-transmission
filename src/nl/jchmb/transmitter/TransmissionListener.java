package nl.jchmb.transmitter;

public interface TransmissionListener<T extends Transmittable> {
	public void onReceive(T transmission);
}
