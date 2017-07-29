package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.Numbers;

/**
 * @author ProPercivalalb
 */
public class PotionList {
	
	public static List<String> potionList = new ArrayList<String>();
	public static Hashtable<String, Integer> potionMap = new Hashtable<String, Integer>();
	
	public static List<String> getPotions() {
		return potionList;
	}
	
	public static String getCustomId(int index) {
		return potionList.get(index);
	}
	
	public static int getPotionId(String customId) {
		return potionMap.get(customId);
	}
	
	public static void addPotion(String customId, int enchantmentId) {
		if(potionMap.containsKey(customId))
			return;
		potionList.add(customId);
		potionMap.put(customId, enchantmentId);
	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/potions.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 2).filter(arr -> Numbers.isInteger(arr[1]));
		parts.forEach(arr -> addPotion(arr[0], Numbers.parse(arr[1])));
	}
}
