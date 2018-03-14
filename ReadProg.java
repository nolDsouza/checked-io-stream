package networks;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
/**
 * Program used in conjunction with WriteProg. Reads input from a file,
 * And prints it out to console. Previous Checksum from seperate file is
 * also outputted. Finally output checksum of new transmission which should
 * be equal.
 */
public class ReadProg {
	private static final String INPUT_FILE = "output.txt";
	private static final String CHKSM_FILE = "checksum.txt";

	public static void main(String args[]) {
		InputStream fIS, cIS;
		CheckedIOStream cIOS, bIOS;
		try {
			fIS = new FileInputStream(INPUT_FILE);
			cIS = new FileInputStream(CHKSM_FILE);
			// Buffered transmitter for checksum
			bIOS = new CheckedIOStream(cIS, System.out);
			// Checked input transmitter
			cIOS = new CheckedIOStream(fIS, System.out, 'x');

			cIOS.transmit();
			System.out.print("Checksum from file:  ");
			// Using CheckedIOStream to read checksum from file
			// calculating checksum not necassary but get's job done
			// without using BufferedReader
			bIOS.transmit();
			String[] report = cIOS.getReport();
			System.out.println("Checksum calculated: " + report[0]);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
