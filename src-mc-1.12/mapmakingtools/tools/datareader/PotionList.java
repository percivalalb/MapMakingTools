package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.NumberParse;

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
		try {
			BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream("/assets/mapmakingtools/data/potions.txt"))); 
			String line = "";
			while((line = paramReader.readLine()) != null) {
				
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				String[] dataParts = line.split(" ~~~ ");
				if(dataParts.length != 2)
					continue;
					
				if(!NumberParse.isInteger(dataParts[1]))
					continue;
				
				String customId = dataParts[0];
				int enchantmentId = NumberParse.getInteger(dataParts[1]);
				
				addPotion(customId, enchantmentId);
			}
	    }
		catch(Exception e) {
			e.printStackTrace();
	    }
	}
}
