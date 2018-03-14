package networks;

import java.io.*;
public class ReadAndReplace {

	/**
	 * Java program that takes one input from System.in,
	 * Replaces all instances of "\s" with "_"
	 * Outputs to console
	 */
	public static void main(String args[]) {
		// BufferedReader can buffer System.in without having to code for each char
		BufferedReader bR = new BufferedReader(new InputStreamReader(System.in));
		OutputStream oS = new DataOutputStream(System.out);
		String input = null;
		try {
			input = bR.readLine();
			input = input.replaceAll(" ", "_");
			// DataOutputStream can only read byte ot byte[] input
			oS.write(input.getBytes());
			oS.write('\n');
		} 
		// readLine and write will throw IO exception
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

