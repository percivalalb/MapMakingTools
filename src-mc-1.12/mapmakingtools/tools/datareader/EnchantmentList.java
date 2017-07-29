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
		try {
			BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream("/assets/mapmakingtools/data/enchantments.txt"))); 
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
				
				addEnchantment(customId, enchantmentId);
			}
	    }
		catch(Exception e) {
			e.printStackTrace();
	    }
	}
}
