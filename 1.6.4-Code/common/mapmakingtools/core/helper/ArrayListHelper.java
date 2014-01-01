package mapmakingtools.core.helper;

import java.util.ArrayList;

/**
 * @author ProPercivalalb
 */
public class ArrayListHelper {
	
	public static ArrayList createArrayList() {
		return new ArrayList();
	}
	
	/**
	 * Checks the list if it is null and empty
	 * @param list The list to be checked
	 * @return If the list contains a value
	 */
	public static boolean isEmpty(ArrayList list) {
		return list == null ? true : list.isEmpty();
	}
	
	public static void removeFirst(ArrayList list) {
		if(isEmpty(list)) return;
		remove(list, 0);
	}
	
	public static void removeLast(ArrayList list) {
		if(isEmpty(list)) return;
		remove(list, list.size() - 1);
	}
	
	public static void remove(ArrayList list, int index) {
		if(isEmpty(list)) return;
		if(!isIndexInBounds(list, index)) return;
		list.remove(index);
	}
	
	public static boolean isIndexInBounds(ArrayList list, int index) {
		return index < 0 ? false : index >= list.size() ? false : true;
	}
}
