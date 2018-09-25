package mapmakingtools.helper;

/**
 * @author ProPercivalalb
 */
public class Numbers {
	
	public static boolean areDoubles(String... texts) {
		for(String text : texts) {
			if(!isDouble(text))
				return false;
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
	
	public static boolean areIntegers(String... texts) {
		for(String text : texts) {
			if(!isInteger(text))
				return false;
		}
		return true;
	}
	
	public static int parse(String text) {
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
