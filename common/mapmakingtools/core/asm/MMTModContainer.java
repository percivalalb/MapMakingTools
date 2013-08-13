package mapmakingtools.core.asm;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import mapmakingtools.api.FilterRegistry;
import mapmakingtools.api.RotationManager;
import mapmakingtools.command.CommandHandler;
import mapmakingtools.common.RotationFurnace;
import mapmakingtools.common.RotationQuatzPillar;
import mapmakingtools.common.RotationVanillaLog;
import mapmakingtools.core.handler.ActionHandler;
import mapmakingtools.core.handler.ConnectionHandler;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.handler.LocalizationHandler;
import mapmakingtools.core.handler.PlayerTrackerHandler;
import mapmakingtools.core.handler.WorldOverlayHandler;
import mapmakingtools.core.helper.DirectoryHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MobSpawnerType;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.filters.FilterBabyMonster;
import mapmakingtools.filters.FilterConvertToDispenser;
import mapmakingtools.filters.FilterConvertToDropper;
import mapmakingtools.filters.FilterCreeperExplosion;
import mapmakingtools.filters.FilterFillInventory;
import mapmakingtools.filters.FilterMobType;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketHandler;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.ModMetadata;
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
public class MMTModContainer extends DummyModContainer {
	
	public MMTModContainer() {
		super(new ModMetadata());
        this.getMetadata();
    }
	
	@Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return false;        
    }
	
	@Override
	public ModMetadata getMetadata() {
	    ModMetadata meta = super.getMetadata();

	    meta.modId       = Reference.MOD_ID + "|Core";
	    meta.name        = Reference.MOD_NAME + "|Core";
	    meta.version     = Reference.MOD_VERSION;
	    meta.authorList  = Reference.MOD_AUTHORS;
	    meta.description = Reference.MOD_DESCRIPTION;
	    meta.url         = Reference.MOD_URL;
	    meta.credits     = Reference.MOD_CREDITS;
	    meta.parent		 = Reference.MOD_ID;

	        
	    return meta;
	}
	
	@Override
	public boolean isNetworkMod() {
	    return true;
	}
}
