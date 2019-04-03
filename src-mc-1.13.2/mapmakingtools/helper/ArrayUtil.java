package mapmakingtools.helper;

public class ArrayUtil {

	public static int indexOf(char[] array, char object) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == object)
				return i;
		}
		return -1;
	}
	
	public static boolean contains(char[] array, char object) {
		return indexOf(array, object) != -1;
	}
}
