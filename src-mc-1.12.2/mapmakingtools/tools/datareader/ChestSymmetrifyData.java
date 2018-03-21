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
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/chestpatterns.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 4 && Numbers.isInteger(arr[0]));
		parts.forEach(arr -> addChestPattern(Numbers.parse(arr[0]), arr[1], arr[2], arr[3]));
	}
}
