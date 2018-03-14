package networks;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
public class ReadProg {
	private static final String INPUT_FILE = "output.txt";
	private static final String CHKSM_FILE = "checksum.txt";

	public static void main(String args[]) {
		InputStream fIS, cIS;
		CheckedIOStream cIOS, bIOS;
		try {
			fIS = new FileInputStream(INPUT_FILE);
			cIS = new FileInputStream(CHKSM_FILE);
			bIOS = new CheckedIOStream(cIS, System.out);
			cIOS = new CheckedIOStream(fIS, System.out, 'x');

			cIOS.transmit();
			System.out.print("Checksum from file:  ");
			bIOS.transmit();
			String[] report = cIOS.getReport();
			System.out.println("Checksum calculated: " + report[0]);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
