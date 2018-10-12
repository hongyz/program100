package text001;

import java.io.Console;

public class Reverser {

	public static void main(String[] args) {
		while (true) {
			System.out.println("input a string: ");
			Console console = System.console();
			if (console == null) {
				System.out.println("Console is null.");
				System.exit(1);
			}
			String s = console.readLine();
			String s2 = "";
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(s.length() - 1 - i);
				s2 += c;
			}
			System.out.println("reverse of <" + s + "> is : <" + s2 + ">");
		}
	}
}
