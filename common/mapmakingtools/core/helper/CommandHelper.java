package mapmakingtools.core.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Strings;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHealth;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * @author ProPercivalalb
 */
public class CommandHelper {

	//Potion Command
	private static Map<Integer, String> intToStringMappingPotion = new HashMap<Integer, String>();
	private static Map<String, Integer> stringToIntMappingPotion = new HashMap<String, Integer>();
	
	private static void addPotion(Potion potion, String name) {
		if(intToStringMappingPotion.containsKey(potion.id)) return;
		intToStringMappingPotion.put(potion.id, name);
		stringToIntMappingPotion.put(name.toLowerCase(), potion.id);
	}
	
	private static void initPotions() {
		addPotion(Potion.moveSpeed, "Speed");
		addPotion(Potion.moveSlowdown, "Slowness");
		addPotion(Potion.digSpeed, "Haste");
		addPotion(Potion.digSlowdown, "MiningFatigue");
		addPotion(Potion.damageBoost, "Strength");
		addPotion(Potion.heal, "Heal");
		addPotion(Potion.harm, "Harm");
		addPotion(Potion.jump, "Jump");
		addPotion(Potion.confusion, "Confusion");
		addPotion(Potion.regeneration, "Regeneration");
		addPotion(Potion.resistance, "Resistance");
		addPotion(Potion.fireResistance, "FireResistance");
		addPotion(Potion.waterBreathing, "waterbreathing");
		addPotion(Potion.invisibility, "Invisibility");
		addPotion(Potion.blindness, "Blindness");
		addPotion(Potion.nightVision, "Nightvision");
		addPotion(Potion.hunger, "Hunger");
		addPotion(Potion.weakness, "Weakness");
		addPotion(Potion.poison, "Poison");
		addPotion(Potion.wither, "Wither");
		addPotion(Potion.field_76434_w, "Healthboost");
		addPotion(Potion.field_76444_x, "Absorption");
		addPotion(Potion.field_76443_y, "Saturation");
	}
	public static String[] getPotionNames() {
		return intToStringMappingPotion.values().toArray(new String[] {});
	}
	public static int getPotionIdFromString(String name) {
		name.toLowerCase();
		if(!stringToIntMappingPotion.containsKey(name)) return -1;
		return stringToIntMappingPotion.get(name);
	}
	
	//Biome Command
	private static Map<Integer, String> intToStringMappingBiome = new HashMap<Integer, String>();
	private static Map<String, Integer> stringToIntMappingBiome = new HashMap<String, Integer>();
		
	private static void addBiome(BiomeGenBase biome, String name) {
		if(intToStringMappingBiome.containsKey(biome.biomeID)) return;
		intToStringMappingBiome.put(biome.biomeID, name.toLowerCase());
		stringToIntMappingBiome.put(name.toLowerCase(), biome.biomeID);
	}
		
	private static void initBiomes() {
		for(int i = 0; i < BiomeGenBase.biomeList.length; ++i) {
			BiomeGenBase biome = BiomeGenBase.biomeList[i];
			if(biome != null && !Strings.isNullOrEmpty(biome.biomeName))
				addBiome(biome, biome.biomeName.replaceAll(" ", ""));
		}
	}
	public static String[] getBiomeNames() {
		return intToStringMappingBiome.values().toArray(new String[] {});
	}
	public static int getBiomeIdFromString(String name) {
		name.toLowerCase();
		if(!stringToIntMappingBiome.containsKey(name)) return -1;
		return stringToIntMappingBiome.get(name);
	}
	
	//End
	
	
	//General
	public static String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
	
	static {
		initPotions();
		initBiomes();
	}
}
