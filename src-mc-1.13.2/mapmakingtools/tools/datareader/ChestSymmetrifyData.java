package mapmakingtools.tools.datareader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import mapmakingtools.helper.Numbers;

/**
 * @author ProPercivalalb
 */
public class ChestSymmetrifyData {

	private static final Map<Integer, String[]> possiblePatterns = new Hashtable<Integer, String[]>();
	
	/**
	 * Adds a possible pattern to a map which then will be looked at when choosing a pattern
	 * @param numberItems The number of items there must be for this pattern to be used
	 * @param pattern The pattern "XOXOXOXOX" 'X' = Item, 'O' = Nothing, must be a length of 9
	 */
	public static void addChestPattern(int numberItems, String... pattern) {
		if(!possiblePatterns.containsKey(numberItems) && pattern.length == 3) {
			int numberOfx = 0;
			for(String row : pattern) {
				if(row.length() != 9)
					return;
				for(int count = 0; count < row.length(); ++count) {
					char cha = row.charAt(count);
					if(cha == 'X' || cha == 'x') {
						++numberOfx;
					}
				}
			}
			if(numberOfx != numberItems)
				return;
			possiblePatterns.put(numberItems, pattern);
		}
	}
	
	public static String[][] getPattern(int numberItems) {
		String[][] str = new String[3][9];
		if(possiblePatterns.containsKey(numberItems)) {
			String[] pattern = possiblePatterns.get(numberItems);
			for(int rows = 0; rows < pattern.length; ++rows) {
				String row = pattern[rows];
				for(int columns = 0; columns < row.length(); ++columns) {
					str[rows][columns] = String.valueOf(row.charAt(columns));
				}
			}
		}
		return str;
	}
	
	/**
	 * Gets the slots the pattern fits
	 * @param numberItems The number of items in the chest which points to the pattern
	 * @return A list of the slots for the items to go in
	 */
	public static Iterator<Integer> getIteratorSlot(int numberItems) {
		List<Integer> list = new ArrayList<Integer>();
		if(possiblePatterns.containsKey(numberItems)) {
			String[] pattern = possiblePatterns.get(numberItems);
			for(int rows = 0; rows < pattern.length; ++rows) {
				String row = pattern[rows];
				for(int columns = 0; columns < row.length(); ++columns) {
					char cha = row.charAt(columns);
					if(cha == 'X' || cha == 'x') {
						list.add(rows * 9 + columns);
					}
				}
			}
		}
		return list.iterator();
	}
	
	public static void readDataFromFile() {
		addChestPattern(1, "ooooooooo", "ooooXoooo", "ooooooooo");
		addChestPattern(2, "ooooooooo", "oooXoXooo", "ooooooooo");
		addChestPattern(3, "ooooooooo", "ooXoXoXoo", "ooooooooo");
		addChestPattern(4, "ooooooooo", "oXoXoXoXo", "ooooooooo");
		addChestPattern(5, "ooooooooo", "XoXoXoXoX", "ooooooooo");
		addChestPattern(6, "ooXoXoXoo", "ooooooooo", "ooXoXoXoo");
		addChestPattern(7, "oooXoXooo", "ooXoXoXoo", "oooXoXooo");
		addChestPattern(8, "oXoXoXoXo", "ooooooooo", "oXoXoXoXo");
		addChestPattern(9, "oXoXoXoXo", "ooooXoooo", "oXoXoXoXo");
		addChestPattern(10, "oXoXoXoXo", "ooXoooXoo", "oXoXoXoXo");
		addChestPattern(11, "oXoXoXoXo", "ooXoXoXoo", "oXoXoXoXo");
		addChestPattern(12, "oXoXoXoXo", "XoXoooXoX", "oXoXoXoXo");
		addChestPattern(13, "oXoXoXoXo", "XoXoXoXoX", "oXoXoXoXo");
		addChestPattern(14, "XoXoXoXoX", "oXoXoXoXo", "XoXoXoXoX");
		addChestPattern(15, "ooXXXXXoo", "ooXXXXXoo", "ooXXXXXoo");
		addChestPattern(16, "ooXXXXXoo", "oXXXoXXXo", "ooXXXXXoo");
		addChestPattern(17, "oXXXXXXXo", "oooXXXooo", "oXXXXXXXo");
		addChestPattern(18, "oXXXoXXXo", "oXXXoXXXo", "oXXXoXXXo");
		addChestPattern(19, "oXXXoXXXo", "oXXXXXXXo", "oXXXoXXXo");
		addChestPattern(20, "oXXXXXXXo", "oXXXoXXXo", "oXXXXXXXo");
		addChestPattern(21, "oXXXXXXXo", "oXXXXXXXo", "oXXXXXXXo");
		addChestPattern(22, "oXXXXXXXo", "XXXXoXXXX", "oXXXXXXXo");
		addChestPattern(23, "oXXXXXXXo", "XXXXXXXXX", "oXXXXXXXo");
		addChestPattern(24, "XXXXXXXXX", "oXXXoXXXo", "XXXXXXXXX");
		addChestPattern(25, "XXXXXXXXX", "oXXXXXXXo", "XXXXXXXXX");
		addChestPattern(26, "XXXXXXXXX", "XXXXoXXXX", "XXXXXXXXX");
		addChestPattern(27, "XXXXXXXXX", "XXXXXXXXX", "XXXXXXXXX");
	}
}
