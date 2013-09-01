package mapmakingtools.core.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHealth;
import net.minecraft.server.MinecraftServer;

/**
 * @author ProPercivalalb
 */
public class CommandHelper {

	//Potion Command
	private static Map<Integer, String> intToStringMapping = new HashMap<Integer, String>();
	private static Map<String, Integer> stringToIntMapping = new HashMap<String, Integer>();
	
	private static void addPotion(Potion potion, String name) {
		if(intToStringMapping.containsKey(potion.id)) return;
		intToStringMapping.put(potion.id, name.toLowerCase());
		stringToIntMapping.put(name.toLowerCase(), potion.id);
	}
	
	private static void initPotions() {
		addPotion(Potion.moveSpeed, "speed");
		addPotion(Potion.moveSlowdown, "slowness");
		addPotion(Potion.digSpeed, "haste");
		addPotion(Potion.digSlowdown, "miningfatigue");
		addPotion(Potion.damageBoost, "strength");
		addPotion(Potion.heal, "heal");
		addPotion(Potion.harm, "harm");
		addPotion(Potion.jump, "jump");
		addPotion(Potion.confusion, "confusion");
		addPotion(Potion.regeneration, "regeneration");
		addPotion(Potion.resistance, "resistance");
		addPotion(Potion.fireResistance, "fireresistance");
		addPotion(Potion.waterBreathing, "waterbreathing");
		addPotion(Potion.invisibility, "invisibility");
		addPotion(Potion.blindness, "blindness");
		addPotion(Potion.nightVision, "nightvision");
		addPotion(Potion.hunger, "hunger");
		addPotion(Potion.weakness, "weakness");
		addPotion(Potion.poison, "poison");
		addPotion(Potion.wither, "wither");
		addPotion(Potion.field_76434_w, "healthboost");
		addPotion(Potion.field_76444_x, "absorption");
		addPotion(Potion.field_76443_y, "saturation");
	}
	public static String[] getPotionNames() {
		return intToStringMapping.values().toArray(new String[] {});
	}
	public static int getPotionIdFromString(String name) {
		name.toLowerCase();
		if(!stringToIntMapping.containsKey(name)) return -1;
		return stringToIntMapping.get(name);
	}
	
	//End
	
	
	//General
	public static String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
	
	static {
		initPotions();
	}
}
