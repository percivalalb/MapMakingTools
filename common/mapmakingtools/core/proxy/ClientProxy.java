package mapmakingtools.core.proxy;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.core.handler.MMTKeyHandler;
import mapmakingtools.core.handler.ScreenRenderHandler;
import mapmakingtools.core.handler.WorldOverlayHandler;
import mapmakingtools.core.helper.MobSpawnerType;
import mapmakingtools.filters.FilterBabyMonster;
import mapmakingtools.filters.FilterChestSymmetrify;
import mapmakingtools.filters.FilterConvertToDispenser;
import mapmakingtools.filters.FilterConvertToDropper;
import mapmakingtools.filters.FilterCreeperExplosion;
import mapmakingtools.filters.FilterEditSign;
import mapmakingtools.filters.FilterFillInventory;
import mapmakingtools.filters.FilterItemSpawner;
import mapmakingtools.filters.FilterMobArmor;
import mapmakingtools.filters.FilterMobMaxHealth;
import mapmakingtools.filters.FilterMobPosition;
import mapmakingtools.filters.FilterMobType;
import mapmakingtools.filters.FilterMobVelocity;
import mapmakingtools.filters.FilterPotionSpawner;
import mapmakingtools.filters.FilterSpawnerTimings;
import mapmakingtools.filters.FilterVillagerShop;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

	@Override
	public void onPreLoad() {
		super.onPreLoad();
		//Blocks
		FilterManager.registerFilter(new FilterFillInventory());
		FilterManager.registerFilter(new FilterConvertToDropper());
		FilterManager.registerFilter(new FilterConvertToDispenser());
		FilterManager.registerFilter(new FilterMobType());
		FilterManager.registerFilter(new FilterSpawnerTimings());
		FilterManager.registerFilter(new FilterMobVelocity());
		FilterManager.registerFilter(new FilterMobPosition());
		FilterManager.registerFilter(new FilterMobArmor());
		FilterManager.registerFilter(new FilterBabyMonster());
		FilterManager.registerFilter(new FilterCreeperExplosion());
		FilterManager.registerFilter(new FilterPotionSpawner());
		FilterManager.registerFilter(new FilterItemSpawner());
		FilterManager.registerFilter(new FilterChestSymmetrify());
		FilterManager.registerFilter(new FilterEditSign());
		
		//Entities
		FilterManager.registerFilter(new FilterVillagerShop());
		FilterManager.registerFilter(new FilterMobMaxHealth());
		
		MobSpawnerType.add("Item");
		MobSpawnerType.add("XPOrb");
		MobSpawnerType.add("Arrow");
		MobSpawnerType.add("Snowball");
		MobSpawnerType.add("Fireball");
		MobSpawnerType.add("SmallFireball");
		MobSpawnerType.add("EyeOfEnderSignal");
		MobSpawnerType.add("ThrownPotion");
		MobSpawnerType.add("ThrownExpBottle");
		MobSpawnerType.add("WitherSkull");
		MobSpawnerType.add("PrimedTnt");
		MobSpawnerType.add("FallingSand");
		MobSpawnerType.add("FireworksRocketEntity");
		MobSpawnerType.add("Boat");
		MobSpawnerType.add("MinecartRideable");
		MobSpawnerType.add("MinecartChest");
		MobSpawnerType.add("MinecartFurnace");
		MobSpawnerType.add("MinecartTNT");
		MobSpawnerType.add("MinecartHopper");
		MobSpawnerType.add("MinecartSpawner");
		MobSpawnerType.add("Creeper");
		MobSpawnerType.add("Skeleton");
		MobSpawnerType.add("Spider");
		MobSpawnerType.add("Giant");
		MobSpawnerType.add("Zombie");
		MobSpawnerType.add("Slime");
		MobSpawnerType.add("Ghast");
		MobSpawnerType.add("PigZombie");
		MobSpawnerType.add("Enderman");
		MobSpawnerType.add("CaveSpider");
		MobSpawnerType.add("Silverfish");
		MobSpawnerType.add("Blaze");
		MobSpawnerType.add("LavaSlime");
		MobSpawnerType.add("EnderDragon");
		MobSpawnerType.add("WitherBoss");
		MobSpawnerType.add("Bat");
		MobSpawnerType.add("Witch");
		MobSpawnerType.add("Pig");
		MobSpawnerType.add("Sheep");
		MobSpawnerType.add("Cow");
		MobSpawnerType.add("Chicken");
		MobSpawnerType.add("Squid");
		MobSpawnerType.add("Wolf");
		MobSpawnerType.add("MushroomCow");
		MobSpawnerType.add("SnowMan");
		MobSpawnerType.add("Ozelot");
		MobSpawnerType.add("VillagerGolem");
		MobSpawnerType.add("Villager");
		MobSpawnerType.add("EnderCrystal");
		MobSpawnerType.add("EntityHorse");
	}
	
	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new WorldOverlayHandler());
		MinecraftForge.EVENT_BUS.register(new ScreenRenderHandler());
		KeyBindingRegistry.registerKeyBinding(new MMTKeyHandler());
	}
}
