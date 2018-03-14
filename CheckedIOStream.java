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
public class CheckedIOStream {
	private static final int EXTRA_CHAR = 1;
	private static final int BUFF_LEN = 1024;
	private static final int TERMINATE_VAL = -2;
	private static final char EOF = (char) -1;
	private char terminatingChar;
	private char nextChar;
	private CheckedInputStream cIS;
	private CheckedOutputStream cOS;
	private int off;
	private byte[] bytes;

	/**
	 * InputStream will be passed as System.in for tests
	 */
	public CheckedIOStream(InputStream iS, OutputStream oS) {
		// char default value
		nextChar = '\u1000';
		terminatingChar = (char) -1;
		bytes = new byte[BUFF_LEN + EXTRA_CHAR];
		off = 0;
		cIS = new CheckedInputStream(iS, new CRC32());
		cOS = new CheckedOutputStream(oS, new CRC32()); 
	}

	public CheckedIOStream(InputStream iS, OutputStream oS, char tC) {
		// char default value
		nextChar = '\u1000';
		terminatingChar = tC;
		bytes = new byte[BUFF_LEN + EXTRA_CHAR];
		off = 0;
		cIS = new CheckedInputStream(iS, new CRC32());
		cOS = new CheckedOutputStream(oS, new CRC32()); 
	}

	private int read() throws IOException {
		for (char c = (char) cIS.read(); c !='\n'; c = (char) cIS.read()) {
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
		if (bytes[0] == terminatingChar && off == 1) {
			cleanUp(cOS.getChecksum());
			return TERMINATE_VAL;
		}
		return off;
	}

	private int write() throws IOException {
		int elems = off;

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

	private void cleanUp(Checksum x) {
		x.update(terminatingChar);
		x.update('\n');
	}
	
	public String[] getReport() {
		String[] checkSums = {String.valueOf(cIS.getChecksum().getValue()), 
			String.valueOf(cOS.getChecksum().getValue())};
		return checkSums;
	}

	public void transmit() throws IOException {
		while (read() != TERMINATE_VAL) {
			write();
		}
	}

}
