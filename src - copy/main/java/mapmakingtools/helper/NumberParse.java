package mapmakingtools.helper;

/**
 * @author ProPercivalalb
 */
public class NumberParse {
	
	public static boolean areIntegers(String... texts) {
		for(String text : texts) {
			try {
				new Integer(text);
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean areDoubles(String... texts) {
		for(String text : texts) {
			try {
				new Double(text);
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isDouble(String text) {
		try {
			new Double(text);
			return true;
		}
		catch(Exception e) {}
		return false;
	}
	
	public static double getDouble(String text) {
		try {
			return new Double(text);
		}
		catch(Exception e) {}
		return 0.0D;
	}
	
	public static int getInteger(String text) {
		try {
			return new Integer(text);
		}
		catch(Exception e) {}
		return 0;
	}

	public static boolean isInteger(String text) {
		try {
			new Integer(text);
			return true;
		}
		catch(Exception e) {}
		return false;
	}
}
