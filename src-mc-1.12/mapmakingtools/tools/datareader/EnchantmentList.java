package mapmakingtools.tools.datareader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import mapmakingtools.helper.Numbers;

/**
 * @author ProPercivalalb
 */
public class EnchantmentList {
	
	public static List<String> enchantmentList = new ArrayList<String>();
	public static Hashtable<String, Integer> enchantmentMap = new Hashtable<String, Integer>();
	
	public static List<String> getEnchantments() {
		return enchantmentList;
	}
	
	public static String getCustomId(int index) {
		return enchantmentList.get(index);
	}
	
	public static int getEnchantmentId(String customId) {
		return enchantmentMap.get(customId);
	}
	
	public static void addEnchantment(String customId, int enchantmentId) {
		if(enchantmentMap.containsKey(customId))
			return;
		enchantmentList.add(customId);
		enchantmentMap.put(customId, enchantmentId);
	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/enchantments.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 2).filter(arr -> Numbers.isInteger(arr[1]));
		parts.forEach(arr -> addEnchantment(arr[0], Numbers.parse(arr[1])));
	}
}
