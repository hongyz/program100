package text003;

import java.io.Console;
import java.util.Arrays;

public class VowelsCounter {

	public static void main(String[] args) {
		while (true) {
			System.out.println("input a string: ");
			Console console = System.console();
			if (console == null) {
				System.out.println("Console is null.");
				System.exit(1);
			}
			String s = console.readLine();
			char[] vowels = new char[]{'A', 'E', 'I', 'O', 'U', 'Y'};
			int[] counts = new int[] {0, 0, 0, 0, 0, 0};
			for (int i = 0; i < s.length(); i++) {
				char c = Character.toUpperCase(s.charAt(i));
				int index = Arrays.binarySearch(vowels, c);
				if (index >= 0) {
					counts[index]++;
				}
			}
			System.out.println("volews of <" + s + "> is :");
			for (int i = 0; i < vowels.length; i++) {
				System.out.println(vowels[i] + ": " + counts[i]);
			}
		}
	}
}
