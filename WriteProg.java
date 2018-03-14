package networks;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 * Program used to read lines from System.in and output them to a file.
 * Checksum of bytes read and written is calculated and if they are the same,
 * outputted to a sperate file.
 */
public class WriteProg {
	private static final String OUTPUT_FILE = "output.txt";
	private static final String CHCKSM_FILE = "checksum.txt";

	public static void main(String args[]) {
		OutputStream fOS, cOS; 
		CheckedIOStream cIOS = null;
		try {
			fOS = new FileOutputStream(OUTPUT_FILE); 
			cOS = new FileOutputStream(CHCKSM_FILE);
			// Terminating character recognises 'x' as end of input.
			cIOS = new CheckedIOStream(System.in, fOS, 'x');

			cIOS.transmit();
			String[] report = cIOS.getReport();
			boolean checked = (report[0].equals(report[1]));
			System.out.printf("Checksums are equal: %s\n", checked);
			// In case checksums aren't equal write both for debugging
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
