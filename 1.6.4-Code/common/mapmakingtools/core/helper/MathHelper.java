package mapmakingtools.core.helper;

/**
 * @author ProPercivalalb
 */
public class MathHelper {
	
	public static int small(int a, int b) {
		return a <= b ? a : b;
	}
	
	public static int big(int a, int b) {
		return a <= b ? b : a;
	}
	
	public static int floor_double(double par0) {
        int i = (int)par0;
        return par0 < (double)i ? i - 1 : i;
    }
}
