package networks;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
public class Q3 {
	private static final String INPUT_FILE = "output.txt";
	private static final String CHCKSM_FILE = "checksum.txt";

	public static void main(String args[]) {
		InputStream fIS, cIS;
		CheckedIOStream cIOS = null;

		try {
			fIS = new FileInputStream(INPUT_FILE);
			cIS = new FileInputStream(CHCKSM_FILE);
			cIOS = new CheckedIOStream(fIS, System.out, 'x');

			cIOS.transmit();
			String[] report = cIOS.getReport();
			System.out.println(report[0]);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
