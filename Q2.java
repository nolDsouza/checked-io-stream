package networks;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class Q2 {
	private static final String OUTPUT_FILE = "output.txt";
	private static final String CHCKSM_FILE = "checksum.txt";

	public static void main(String args[]) {
		OutputStream fOS, cOS; 
		CheckedIOStream cIOS = null;
		try {
			fOS = new FileOutputStream(OUTPUT_FILE); 
			cOS = new FileOutputStream(CHCKSM_FILE);
			cIOS = new CheckedIOStream(System.in, fOS, 'x');

			cIOS.transmit();
			String[] report = cIOS.getReport();
			boolean checked = (report[0].equals(report[1]));
			System.out.printf("Checksums are equal: %s\n", checked);
			if (!checked) {
				cOS.write("Input Stream Checksum - ".getBytes());
				cOS.write(report[0].getBytes());
				cOS.write("\nOutput Stream Checksum - ".getBytes());
			}
			cOS.write(report[1].getBytes());
			cOS.write('\n');
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
