package networks;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.Arrays;
import java.lang.ArrayIndexOutOfBoundsException;
/**
 * This class is used to implement IO, between two streams, only transmits once
 * It reads input by individual byte, writes after filling a buffer of fixed 
 * size. Checksum is calculated thanks to CheckedInput/OutputStream classes.
 * Read and Write methods are private, only functionality is to transmit.
 */
public class CheckedIOStream {
	private static final int EXTRA_CHAR = 1;
	private static final int BUFF_LEN = 1024;
	private static final int TERMINATE_VAL = -2;
	private static final char EOF = (char) -1;
	// Optional parameter to be passed
	private char terminatingChar;
	private char nextChar;
	private CheckedInputStream cIS;
	private CheckedOutputStream cOS;
	private int off;
	private byte[] bytes;

	/**
	 * InputStream will be passed as System.in for tests.
	 */
	public CheckedIOStream(InputStream iS, OutputStream oS) {
		// char default value
		nextChar = '\u1000';
		terminatingChar = EOF;
		bytes = new byte[BUFF_LEN + EXTRA_CHAR];
		off = 0;
		// CheckedStream calculates checksum for all read/writes
		cIS = new CheckedInputStream(iS, new CRC32());
		cOS = new CheckedOutputStream(oS, new CRC32()); 
	}

	public CheckedIOStream(InputStream iS, OutputStream oS, char tC) {
		// char default value
		nextChar = '\u1000';
		// In the case that System.in is inputStream, needs a terminal character
		terminatingChar = tC;
		bytes = new byte[BUFF_LEN + EXTRA_CHAR];
		off = 0;
		cIS = new CheckedInputStream(iS, new CRC32());
		cOS = new CheckedOutputStream(oS, new CRC32()); 
	}
	
	/**
	 * One iteration of read() will store one line of input into the 
	 * byte[] buffer. Unless the line is longer than the buffer, in which
	 * buffer must be emptied first. off is equal to number of stored elements.
	 * @return number of elements to be written.
	 */
	private int read() throws IOException {
		for (char c = (char) cIS.read(); c !='\n'; c = (char) cIS.read()) {
			// EOF reached when InputStream is finite
			if (c == EOF) {
				cleanUp(cIS.getChecksum());
				cleanUp(cOS.getChecksum());
				return TERMINATE_VAL;
			}
			byte b = (byte) c;
			bytes[off] = b;
			off++;
			if (off > BUFF_LEN) {
				// System.err.println("Info: buffer overflow");
				return BUFF_LEN;
			}
		}
		// In case that system.in receives terminating character
		if (bytes[0] == terminatingChar && off == 1) {
			// If terminatigChar was read, output stream must calculate
			// the checksum without writing explicitly
			cleanUp(cOS.getChecksum());
			return TERMINATE_VAL;
		}
		return off;
	}
	
	/** 
	 * One iteration of write() will write each element of the byte[] buffer
	 * to the supplied outputstream. Buffer overflows are detected when 
	 * off greater than BUFF_LEN. In this case no newline is written.
	 */
	private int write() throws IOException {
		// store elems as off must be mutated and counted
		int elems = off;
		
		// off may be less that buffer total length
		for (int i=0; i<off; i++) {
			cOS.write(bytes[i]);
			elems--;
		}
		// New line is not stored in bytes, so must be written explicitely
		if (off <= BUFF_LEN) {
			cOS.write('\n');
		}
		return (off = elems);
	}

	/**
	 * @param Checksum CRC32 or Adler32 to be updated
	 * If the terminatingChar is read, it will be caulculated in the checksum
	 * subsequent reads and write must include terminatingChar in calcuations
	 * otherwise the checksum will not be equal.
	 * TODO Code for when terminatigChar was actually used.
	 */
	private void cleanUp(Checksum x) {
		x.update(terminatingChar);
		x.update('\n');
	}
	
	/**
	 * @return Pair of calculated checksums.
	 */
	public String[] getReport() {
		String[] checkSums = {String.valueOf(cIS.getChecksum().getValue()), 
			String.valueOf(cOS.getChecksum().getValue())};
		return checkSums;
	}
	
	/**
	 * Read and Write are private as calling them incorrectly can
	 * render the checksum useless, therefore transmit will do both at
	 * the same time.
	 */
	public void transmit() throws IOException {
		while (read() != TERMINATE_VAL) {
			write();
		}
	}

}
