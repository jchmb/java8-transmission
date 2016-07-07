package nl.jchmb.transmitter;

import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TransmissionIO {
	/**
	 * Write an integer to the buffer.
	 * 
	 * @param buffer
	 * @param value
	 * @throws TransmitterException
	 */
	public static void writeInteger(ByteBuffer buffer, int value) throws TransmitterException {
		buffer.putInt(value);
	}
	
	/**
	 * Read an integer from the buffer.
	 * 
	 * @param buffer
	 * @return
	 * @throws TransmitterException
	 */
	public static int readInteger(ByteBuffer buffer) throws TransmitterException {
		if (buffer.remaining() < Integer.BYTES) {
			throw new TransmitterException("Not enough bytes to read an integer.");
		}
		return buffer.getInt();
	}
	
	/**
	 * Write a float to the buffer.
	 * 
	 * @param buffer
	 * @param value
	 * @throws TransmitterException
	 */
	public static void writeFloat(ByteBuffer buffer, float value) throws TransmitterException {
		buffer.putFloat(value);
	}
	
	/**
	 * Read a float from the buffer.
	 * 
	 * @param buffer
	 * @return
	 * @throws TransmitterException
	 */
	public static float readFloat(ByteBuffer buffer) throws TransmitterException {
		if (buffer.remaining() < Float.BYTES) {
			throw new TransmitterException("Not enough bytes to read a float.");
		}
		return buffer.getFloat();
	}
	
	/**
	 * Write a double to the buffer.
	 * 
	 * @param buffer
	 * @param value
	 * @throws TransmitterException
	 */
	public static void writeDouble(ByteBuffer buffer, double value) throws TransmitterException {
		buffer.putDouble(value);
	}
	
	/**
	 * Read a double from the buffer.
	 * 
	 * @param buffer
	 * @return
	 * @throws TransmitterException
	 */
	public static double readDouble(ByteBuffer buffer) throws TransmitterException {
		if (buffer.remaining() < Double.BYTES) {
			throw new TransmitterException("Not enough bytes to read a double.");
		}
		return buffer.getDouble();
	}
	
	/**
	 * Write a character to the buffer.
	 * 
	 * @param buffer
	 * @param value
	 * @throws TransmitterException
	 */
	public static void writeCharacter(ByteBuffer buffer, char value) throws TransmitterException {
		buffer.putChar(value);
	}
	
	/**
	 * Read a character from the buffer.
	 * 
	 * @param buffer
	 * @return
	 * @throws TransmitterException
	 */
	public static char readCharacter(ByteBuffer buffer) throws TransmitterException {
		if (buffer.remaining() < Float.BYTES) {
			throw new TransmitterException("Not enough bytes to read a character.");
		}
		return buffer.getChar();
	}
	
	/**
	 * Writer a String to the buffer.
	 * 
	 * @param buffer
	 * @param value
	 * @throws TransmitterException
	 */
	public static void writeString(ByteBuffer buffer, String value) throws TransmitterException {
		int length = value.length();
		writeInteger(buffer, length);
		for (int i = 0; i < length; i++) {
			writeCharacter(buffer, value.charAt(i));
		}
	}
	
	/**
	 * Read a String from the buffer.
	 * 
	 * @param buffer
	 * @return
	 * @throws TransmitterException
	 */
	public static String readString(ByteBuffer buffer) throws TransmitterException {
		int length = readInteger(buffer);
		if (buffer.remaining() < length) {
			throw new TransmitterException("Not enough bytes to read a String.");
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(readCharacter(buffer));
		}
		return builder.toString();
	}
	
	/**
	 * Write an Array to the buffer, given a BufferWriter.
	 * 
	 * @param buffer
	 * @param values
	 * @param writer
	 * @throws TransmitterException
	 */
	public static <T> void writeArray(
			ByteBuffer buffer,
			T[] values,
			BufferWriter<T> writer
	) throws TransmitterException {
		int length = values.length;
		for (int i = 0; i < length; i++) {
			writer.write(buffer, values[i]);
		}
	}
	
	/**
	 * Read an Array from the buffer, given a BufferReader.
	 * 
	 * @param buffer
	 * @param reader
	 * @return
	 * @throws TransmitterException
	 */
	public static <T> T[] readArray(ByteBuffer buffer, BufferReader<T> reader) throws TransmitterException {
		int length = readInteger(buffer);
		@SuppressWarnings("unchecked")
		T[] values = (T[]) new Object[length];
		if (buffer.remaining() < length) {
			throw new TransmitterException("Not enough bytes to read an array.");
		}
		for (int i = 0; i < length; i++) {
			values[i] = reader.read(buffer);
		}
		return values;
	}
	
	/**
	 * Write a Collection to the buffer, given a BufferWriter.
	 * 
	 * @param buffer
	 * @param values
	 * @param writer
	 * @throws TransmitterException
	 */
	public static <T> void writeCollection(
			ByteBuffer buffer,
			Collection<T> values,
			BufferWriter<T> writer
	) throws TransmitterException {
		writeInteger(buffer, values.size());
		for (T value : values) {
			writer.write(buffer, value);
		}
	}
	
	/**
	 * Read a Collection from the buffer,
	 * given a BufferReader and a Collection Supplier.
	 * 
	 * @param buffer
	 * @param reader
	 * @return
	 * @throws TransmitterException
	 */
	public static <T, C extends Collection<T>> C readCollection(
			ByteBuffer buffer,
			BufferReader<T> reader,
			Supplier<C> collectionSupplier
	) throws TransmitterException {
		int length = readInteger(buffer);
		if (buffer.remaining() < length) {
			throw new TransmitterException("Not enough bytes to read a Collection.");
		}
		return IntStream.range(0, length)
				.mapToObj(
						i -> {
							try {
								return reader.read(buffer);
							} catch (TransmitterException e) {
								return null;
							}
						}
				)
				.collect(Collectors.toCollection(collectionSupplier));
	}
	
	/**
	 * Reads a List from the buffer, given a BufferReader.
	 * 
	 * @param buffer
	 * @param reader
	 * @return
	 * @throws TransmitterException
	 */
	public static <T> List<T> readList(
			ByteBuffer buffer,
			BufferReader<T> reader
	) throws TransmitterException {
		return readCollection(buffer, reader, () -> new ArrayList<T>());
	}
	
	/**
	 * Write a Map to the buffer, given a BufferWriter for keys and values.
	 * 
	 * @param buffer
	 * @param values
	 * @param keyWriter
	 * @param valueWriter
	 * @throws TransmitterException
	 */
	public static <K, V> void writeMap(
			ByteBuffer buffer,
			Map<K, V> values,
			BufferWriter<K> keyWriter,
			BufferWriter<V> valueWriter
	) throws TransmitterException {
		writeInteger(buffer, values.size());
		for (Map.Entry<K, V> entry : values.entrySet()) {
			keyWriter.write(buffer, entry.getKey());
			valueWriter.write(buffer, entry.getValue());
		}
	}
	
	/**
	 * Read a Map from the buffer, given a BufferReader for keys and values.
	 * 
	 * @param buffer
	 * @param keyReader
	 * @param valueReader
	 * @return
	 * @throws TransmitterException
	 */
	public static <K, V> Map<K, V> readMap(
			ByteBuffer buffer,
			BufferReader<K> keyReader,
			BufferReader<V> valueReader
	) throws TransmitterException {
		int length = readInteger(buffer);
		if (buffer.remaining() < length) {
			throw new TransmitterException("Not enough bytes to read a Map.");
		}
		return IntStream.range(0, length)
				.sorted()
				.mapToObj(
					i -> new AbstractMap.SimpleEntry<>(
						readWithoutException(buffer, keyReader),
						readWithoutException(buffer, valueReader)
					)
				)
				.collect(
					Collectors.toMap(
						entry -> entry.getKey(),
						entry -> entry.getValue()
					)
				);
	}
	
	public static <T> T readWithoutException(ByteBuffer buffer, BufferReader<T> reader) {
		try {
			return reader.read(buffer);
		} catch (TransmitterException e) {
			return null;
		}
	}
}
