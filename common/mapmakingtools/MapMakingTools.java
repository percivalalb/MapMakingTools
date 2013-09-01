package mapmakingtools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.minecraft.block.Block;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.google.common.eventbus.Subscribe;

import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.api.manager.ForceKillManager;
import mapmakingtools.api.manager.RotationManager;
import mapmakingtools.command.CommandHandler;
import mapmakingtools.common.killentities.KillAll;
import mapmakingtools.common.killentities.KillGeneric;
import mapmakingtools.common.killentities.KillItem;
import mapmakingtools.common.rotation.RotationAnvil;
import mapmakingtools.common.rotation.RotationBed;
import mapmakingtools.common.rotation.RotationButton;
import mapmakingtools.common.rotation.RotationChest;
import mapmakingtools.common.rotation.RotationDispenser;
import mapmakingtools.common.rotation.RotationDoor;
import mapmakingtools.common.rotation.RotationDropper;
import mapmakingtools.common.rotation.RotationFenceGate;
import mapmakingtools.common.rotation.RotationFurnace;
import mapmakingtools.common.rotation.RotationHayBale;
import mapmakingtools.common.rotation.RotationHopper;
import mapmakingtools.common.rotation.RotationLadder;
import mapmakingtools.common.rotation.RotationPistonBase;
import mapmakingtools.common.rotation.RotationPoweredRail;
import mapmakingtools.common.rotation.RotationPumpkin;
import mapmakingtools.common.rotation.RotationQuatzPillar;
import mapmakingtools.common.rotation.RotationRedstoneComparator;
import mapmakingtools.common.rotation.RotationRedstoneRepeater;
import mapmakingtools.common.rotation.RotationSignPost;
import mapmakingtools.common.rotation.RotationSignWall;
import mapmakingtools.common.rotation.RotationStairs;
import mapmakingtools.common.rotation.RotationTorch;
import mapmakingtools.common.rotation.RotationTrapdoor;
import mapmakingtools.common.rotation.RotationTripwireSource;
import mapmakingtools.common.rotation.RotationVanillaLog;
import mapmakingtools.common.rotation.RotationVanillaTrack;
import mapmakingtools.core.handler.ActionHandler;
import mapmakingtools.core.handler.ConfigurationHandler;
import mapmakingtools.core.handler.ConnectionHandler;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.handler.LocalizationHandler;
import mapmakingtools.core.handler.PlayerTrackerHandler;
import mapmakingtools.core.handler.WorldOverlayHandler;
import mapmakingtools.core.helper.ChestSymmetrifyHelper;
import mapmakingtools.core.helper.DirectoryHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MobSpawnerType;
import mapmakingtools.core.helper.VersionHelper;
import mapmakingtools.core.helper.VersionHelper.Type;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.filters.FilterBabyMonster;
import mapmakingtools.filters.FilterConvertToDispenser;
import mapmakingtools.filters.FilterConvertToDropper;
import mapmakingtools.filters.FilterCreeperExplosion;
import mapmakingtools.filters.FilterFillInventory;
import mapmakingtools.filters.FilterMobType;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 * The Main Mod Class.
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
@NetworkMod(clientSideRequired = false, serverSideRequired = true)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools instance;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy proxy;
	
	public static String sectionSign = "";
	
	public MapMakingTools() {
   	 	instance = this;
    }
	
	@EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        CommandHandler.initCommands(event);
    }
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.onPreLoad();
		ConfigurationHandler.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
		
		VersionHelper.checkVersion(Type.BLANK);
		//Loads the Languages into the game
		LocalizationHandler.loadLanguages();
		//Sets the mc directory
		DirectoryHelper.setMcDir(event);
		//Loads the Items
		ModItems.inti();
		
		try {
            BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream("/assets/mapmakingtools/symbol.txt"))); 
            sectionSign = paramReader.readLine();
		}
		catch(Exception e) {}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.instance().registerChannel(new PacketHandler(), Reference.CHANNEL_NAME);
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);
		//Action Handler
		MinecraftForge.EVENT_BUS.register(new ActionHandler());
		MinecraftForge.EVENT_BUS.register(new ConnectionHandler());
		GameRegistry.registerPlayerTracker(new PlayerTrackerHandler());
		proxy.registerHandlers();
	}
		
	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent par1) {
		RotationManager.registerRotationHandler(Block.wood.blockID, new RotationVanillaLog());
		RotationManager.registerRotationHandler(Block.blockNetherQuartz.blockID, new RotationQuatzPillar());
		RotationManager.registerRotationHandler(Block.furnaceIdle.blockID, new RotationFurnace());
		RotationManager.registerRotationHandler(Block.furnaceBurning.blockID, new RotationFurnace());
		RotationManager.registerRotationHandler(Block.dropper.blockID, new RotationDropper());
		RotationManager.registerRotationHandler(Block.dispenser.blockID, new RotationDispenser());
		RotationManager.registerRotationHandler(Block.chest.blockID, new RotationChest());
		RotationManager.registerRotationHandler(Block.chestTrapped.blockID, new RotationChest());
		RotationManager.registerRotationHandler(Block.enderChest.blockID, new RotationChest());
		RotationManager.registerRotationHandler(Block.anvil.blockID, new RotationAnvil());
		RotationManager.registerRotationHandler(Block.stairsWoodOak.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsWoodSpruce.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsWoodBirch.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsWoodJungle.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsBrick.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsStoneBrick.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsCobblestone.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsNetherBrick.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsSandStone.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.stairsNetherQuartz.blockID, new RotationStairs());
		RotationManager.registerRotationHandler(Block.field_111038_cB.blockID, new RotationHayBale());
		RotationManager.registerRotationHandler(Block.signPost.blockID, new RotationSignPost());
		RotationManager.registerRotationHandler(Block.signWall.blockID, new RotationSignWall());
		RotationManager.registerRotationHandler(Block.rail.blockID, new RotationVanillaTrack());
		RotationManager.registerRotationHandler(Block.railPowered.blockID, new RotationPoweredRail());
		RotationManager.registerRotationHandler(Block.railDetector.blockID, new RotationPoweredRail());
		RotationManager.registerRotationHandler(Block.railActivator.blockID, new RotationPoweredRail());
		RotationManager.registerRotationHandler(Block.hopperBlock.blockID, new RotationHopper());
		RotationManager.registerRotationHandler(Block.bed.blockID, new RotationBed());
		RotationManager.registerRotationHandler(Block.trapdoor.blockID, new RotationTrapdoor());
		RotationManager.registerRotationHandler(Block.fenceGate.blockID, new RotationFenceGate());
		RotationManager.registerRotationHandler(Block.pumpkin.blockID, new RotationPumpkin());
		RotationManager.registerRotationHandler(Block.pumpkinLantern.blockID, new RotationPumpkin());
		RotationManager.registerRotationHandler(Block.woodenButton.blockID, new RotationButton());
		RotationManager.registerRotationHandler(Block.stoneButton.blockID, new RotationButton());
		RotationManager.registerRotationHandler(Block.tripWireSource.blockID, new RotationTripwireSource());
		//TODO Complete door rotation
		//RotationManager.registerRotationHandler(Block.doorWood.blockID, new RotationDoor());
		//RotationManager.registerRotationHandler(Block.doorIron.blockID, new RotationDoor());
		//RotationManager.registerRotationHandler(Block.pistonBase.blockID, new RotationPistonBase());
		//RotationManager.registerRotationHandler(Block.pistonStickyBase.blockID, new RotationPistonBase());
		RotationManager.registerRotationHandler(Block.ladder.blockID, new RotationLadder());
		RotationManager.registerRotationHandler(Block.torchWood.blockID, new RotationTorch());
		RotationManager.registerRotationHandler(Block.torchRedstoneIdle.blockID, new RotationTorch());
		RotationManager.registerRotationHandler(Block.torchRedstoneActive.blockID, new RotationTorch());
		RotationManager.registerRotationHandler(Block.redstoneRepeaterIdle.blockID, new RotationRedstoneRepeater());
		RotationManager.registerRotationHandler(Block.redstoneRepeaterActive.blockID, new RotationRedstoneRepeater());
		RotationManager.registerRotationHandler(Block.redstoneComparatorIdle.blockID, new RotationRedstoneComparator());
		RotationManager.registerRotationHandler(Block.redstoneComparatorActive.blockID, new RotationRedstoneComparator());
		
		ForceKillManager.registerHandler("all", new KillAll());
		ForceKillManager.registerHandler("enderman", new KillGeneric(EntityEnderman.class));
		ForceKillManager.registerHandler("blaze", new KillGeneric(EntityBlaze.class));
		ForceKillManager.registerHandler("cow", new KillGeneric(EntityCow.class));
		ForceKillManager.registerHandler("sheep", new KillGeneric(EntitySheep.class));
		ForceKillManager.registerHandler("item", new KillItem());
		ForceKillManager.registerHandler("zombie", new KillGeneric(EntityZombie.class));
		ForceKillManager.registerHandler("skeleton", new KillGeneric(EntitySkeleton.class));
		ForceKillManager.registerHandler("pigzombie", new KillGeneric(EntityPigZombie.class));
		ForceKillManager.registerHandler("giant", new KillGeneric(EntityGiantZombie.class));
		ForceKillManager.registerHandler("cavespider", new KillGeneric(EntityCaveSpider.class));
		ForceKillManager.registerHandler("creeper", new KillGeneric(EntityCreeper.class));
		ForceKillManager.registerHandler("ghast", new KillGeneric(EntityGhast.class));
		ForceKillManager.registerHandler("snowman", new KillGeneric(EntitySnowman.class));
		ForceKillManager.registerHandler("irongolem", new KillGeneric(EntityIronGolem.class));
		ForceKillManager.registerHandler("magmacube", new KillGeneric(EntityMagmaCube.class));
		ForceKillManager.registerHandler("silverfish", new KillGeneric(EntitySilverfish.class));
		ForceKillManager.registerHandler("slime", new KillGeneric(EntitySlime.class));
		ForceKillManager.registerHandler("bat", new KillGeneric(EntityBat.class));
		ForceKillManager.registerHandler("chicken", new KillGeneric(EntityChicken.class));
		ForceKillManager.registerHandler("horse", new KillGeneric(EntityHorse.class));
		ForceKillManager.registerHandler("mooshroom", new KillGeneric(EntityMooshroom.class));
		ForceKillManager.registerHandler("pig", new KillGeneric(EntityPig.class));
		ForceKillManager.registerHandler("pig", new KillGeneric(EntityPig.class));
		ForceKillManager.registerHandler("pig", new KillGeneric(EntityPig.class));
		ForceKillManager.registerHandler("pig", new KillGeneric(EntityPig.class));
		//ForceKillManager.registerHandler("", new KillGeneric(Entity.class));
		//ForceKillManager.registerHandler("", new KillGeneric(Entity.class));
		//ForceKillManager.registerHandler("", new KillGeneric(Entity.class));
		//ForceKillManager.registerHandler("", new KillGeneric(Entity.class));
		
		ChestSymmetrifyHelper.addChestPattern(1, "ooooooooo", "ooooXoooo", "ooooooooo"); //1 Item
		ChestSymmetrifyHelper.addChestPattern(2, "ooooooooo", "oooXoXooo", "ooooooooo"); //2 Items
		ChestSymmetrifyHelper.addChestPattern(3, "ooooooooo", "ooXoXoXoo", "ooooooooo"); //3 Items
		ChestSymmetrifyHelper.addChestPattern(4, "ooooooooo", "oXoXoXoXo", "ooooooooo"); //4 Items
		ChestSymmetrifyHelper.addChestPattern(5, "ooooooooo", "XoXoXoXoX", "ooooooooo"); //5 Items
		ChestSymmetrifyHelper.addChestPattern(6, "ooXoXoXoo", "ooooooooo", "ooXoXoXoo"); //6 Items
		ChestSymmetrifyHelper.addChestPattern(7, "oooXoXooo", "ooXoXoXoo", "oooXoXooo"); //7 Items
		ChestSymmetrifyHelper.addChestPattern(8, "oXoXoXoXo", "ooooooooo", "oXoXoXoXo"); //8 Items
		ChestSymmetrifyHelper.addChestPattern(9, "oXoXoXoXo", "ooooXoooo", "oXoXoXoXo"); //9 Items
		ChestSymmetrifyHelper.addChestPattern(10, "oXoXoXoXo", "ooXoooXoo", "oXoXoXoXo"); //10 Items
		ChestSymmetrifyHelper.addChestPattern(11, "oXoXoXoXo", "ooXoXoXoo", "oXoXoXoXo"); //11 Items
		ChestSymmetrifyHelper.addChestPattern(12, "oXoXoXoXo", "XoXoooXoX", "oXoXoXoXo"); //12 Items
		ChestSymmetrifyHelper.addChestPattern(13, "oXoXoXoXo", "XoXoXoXoX", "oXoXoXoXo"); //13 Items
		ChestSymmetrifyHelper.addChestPattern(14, "XoXoXoXoX", "oXoXoXoXo", "XoXoXoXoX"); //14 Items
		ChestSymmetrifyHelper.addChestPattern(15, "ooXXXXXoo", "ooXXXXXoo", "ooXXXXXoo"); //15 Items
		ChestSymmetrifyHelper.addChestPattern(16, "ooXXXXXoo", "oXXXoXXXo", "ooXXXXXoo"); //16 Items
		ChestSymmetrifyHelper.addChestPattern(17, "oXXXXXXXo", "oooXXXooo", "oXXXXXXXo"); //17 Items
		ChestSymmetrifyHelper.addChestPattern(18, "oXXXoXXXo", "oXXXoXXXo", "oXXXoXXXo"); //18 Items
		ChestSymmetrifyHelper.addChestPattern(19, "oXXXoXXXo", "oXXXXXXXo", "oXXXoXXXo"); //19 Items
		ChestSymmetrifyHelper.addChestPattern(20, "oXXXXXXXo", "oXXXoXXXo", "oXXXXXXXo"); //20 Items
		ChestSymmetrifyHelper.addChestPattern(21, "oXXXXXXXo", "oXXXXXXXo", "oXXXXXXXo"); //21 Items
		ChestSymmetrifyHelper.addChestPattern(22, "oXXXXXXXo", "XXXXoXXXX", "oXXXXXXXo"); //22 Items
		ChestSymmetrifyHelper.addChestPattern(23, "oXXXXXXXo", "XXXXXXXXX", "oXXXXXXXo"); //23 Items
		ChestSymmetrifyHelper.addChestPattern(24, "XXXXXXXXX", "oXXXoXXXo", "XXXXXXXXX"); //24 Items
		ChestSymmetrifyHelper.addChestPattern(25, "XXXXXXXXX", "oXXXXXXXo", "XXXXXXXXX"); //25 Items
		ChestSymmetrifyHelper.addChestPattern(26, "XXXXXXXXX", "XXXXoXXXX", "XXXXXXXXX"); //26 Items
		ChestSymmetrifyHelper.addChestPattern(27, "XXXXXXXXX", "XXXXXXXXX", "XXXXXXXXX"); //27 Items
	}
}
