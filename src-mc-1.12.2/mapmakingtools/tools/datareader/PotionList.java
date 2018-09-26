package mapmakingtools.tools.datareader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import mapmakingtools.helper.Numbers;
import net.minecraft.potion.Potion;

/**
 * @author ProPercivalalb
 */
public class PotionList {
	
	public static List<Potion> potionList = new ArrayList<Potion>();
	
	public static List<Potion> getPotions() {
		return potionList;
	}
	
	public static void addPotion(int enchantmentId) {
		potionList.add(Potion.getPotionById(enchantmentId));

	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/potions.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 2).filter(arr -> Numbers.isInteger(arr[1]));
		parts.forEach(arr -> addPotion(Numbers.parse(arr[1])));
	}
}
