package text002;

public class PigLatin {

	public static void main(String[] args) {
		String s = "banana";
		String s2 = "";
		String prefix = s.substring(0, 1);
		String remain = s.substring(1);
		s2 = remain + prefix + "ay";
		System.out.println(s2);
	}
}
