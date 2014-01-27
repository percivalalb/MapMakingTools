package mapmakingtools.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ProPercivalalb
 */
public class MobSpawnerType {

	private static List<String> validMobs = new ArrayList<String>();
	
	public static void addToList(String mobId) {
		validMobs.add(mobId);
	}
	
	public static void addToList(List<String> list) {
		validMobs.addAll(list);
	}
	
	public static List<String> getSpawnerMobs() {
		return validMobs;
	}
	
	public static void sort() {
		Collections.sort(validMobs);
	}
	
	static {
		addToList("Item");
		addToList("XPOrb");
		addToList("Arrow");
		addToList("Snowball");
		addToList("Fireball");
		addToList("SmallFireball");
		addToList("EyeOfEnderSignal");
		addToList("ThrownPotion");
		addToList("ThrownExpBottle");
		addToList("WitherSkull");
		addToList("PrimedTnt");
		addToList("FallingSand");
		addToList("FireworksRocketEntity");
		addToList("Boat");
		addToList("MinecartRideable");
		addToList("MinecartChest");
		addToList("MinecartFurnace");
		addToList("MinecartTNT");
		addToList("MinecartHopper");
		addToList("MinecartSpawner");
		addToList("Creeper");
		addToList("Skeleton");
		addToList("Spider");
		addToList("Giant");
		addToList("Zombie");
		addToList("Slime");
		addToList("Ghast");
		addToList("PigZombie");
		addToList("Enderman");
		addToList("CaveSpider");
		addToList("Silverfish");
		addToList("Blaze");
		addToList("LavaSlime");
		addToList("EnderDragon");
		addToList("WitherBoss");
		addToList("Bat");
		addToList("Witch");
		addToList("Pig");
		addToList("Sheep");
		addToList("Cow");
		addToList("Chicken");
		addToList("Squid");
		addToList("Wolf");
		addToList("MushroomCow");
		addToList("SnowMan");
		addToList("Ozelot");
		addToList("VillagerGolem");
		addToList("Villager");
		addToList("EnderCrystal");
		//TODO Fix crash addToList("EntityHorse");
	}
}
