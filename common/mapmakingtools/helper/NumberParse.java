package mapmakingtools.helper;

/**
 * @author ProPercivalalb
 */
public class NumberParse {

	public static boolean isNumber(String text) {
		try {
			new Integer(text);
			return true;
		}
		catch(Exception e) {}
		return false;
	}
	
	public static int getNumber(String text) {
		try {
			return new Integer(text);
		}
		catch(Exception e) {}
		return 0;
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
}
