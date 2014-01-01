package mapmakingtools.core.proxy;

import java.util.List;

import mapmakingtools.api.IFilter;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.client.gui.GuiSkull;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.client.gui.GuiWatchPlayer;
import mapmakingtools.core.util.WrenchTasks;
import mapmakingtools.filters.server.FilterServerCommandBlockAlias;
import mapmakingtools.filters.server.FilterServerFrameDropChance;
import mapmakingtools.filters.server.FilterServerMobMaxHealth;
import mapmakingtools.filters.server.FilterServerChestSymmetrify;
import mapmakingtools.filters.server.FilterServerEditSign;
import mapmakingtools.filters.server.FilterServerItemSpawner;
import mapmakingtools.filters.server.FilterServerPotionSpawner;
import mapmakingtools.filters.server.FilterServerMobArmor;
import mapmakingtools.filters.server.FilterServerBabyMonster;
import mapmakingtools.filters.server.FilterServerConvertToDispenser;
import mapmakingtools.filters.server.FilterServerConvertToDropper;
import mapmakingtools.filters.server.FilterServerCreeperExplosion;
import mapmakingtools.filters.server.FilterServerFillInventory;
import mapmakingtools.filters.server.FilterServerMobPosition;
import mapmakingtools.filters.server.FilterServerMobType;
import mapmakingtools.filters.server.FilterServerMobVelocity;
import mapmakingtools.filters.server.FilterServerSpawnerTimings;
import mapmakingtools.filters.server.FilterServerVillagerShop;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.inventory.ContainerItemEditor;
import mapmakingtools.inventory.ContainerWatchPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

	public static final int GUI_ID_SPAWNER_SETTING = 1;
	public static final int GUI_ID_SKULL_NAME 	   = 2;
	public static final int GUI_ID_ITEM_EDITOR 	   = 3;
	public static final int GUI_ID_FILTERS_1 	   = 4;
	public static final int GUI_ID_FILTERS_2 	   = 5;
	public static final int GUI_ID_WATCH_PLAYER    = 6;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == GUI_ID_SPAWNER_SETTING) {} //Do nothing on server
		else if(ID == GUI_ID_SKULL_NAME) {} //Do nothing on server
		else if(ID == GUI_ID_ITEM_EDITOR) {
			return new ContainerItemEditor(player, x);
		}
		else if(ID == GUI_ID_FILTERS_1) {
		    List<IServerFilter> filters = FilterManager.getApplicableServerFilters(player, world, x, y, z);
		    if(!filters.isEmpty()) {
		    	WrenchTasks.addTaskBlock(player, world, x, y, z);
		    	return new ContainerFilter(player, filters).setCor(x, y, z);
		    }
		}
		else if(ID == GUI_ID_FILTERS_2) {
		    List<IServerFilter> filters = FilterManager.getApplicableEntityServerFilters(player, world, x);
		    if(!filters.isEmpty()) {
		    	WrenchTasks.addTaskEntity(player, world, x);
		    	return new ContainerFilter(player, filters).setEntityId(x);
		    }
		}
		else if(ID == GUI_ID_WATCH_PLAYER) {
			return new ContainerWatchPlayer(player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) { 
		if(ID == GUI_ID_SPAWNER_SETTING) {
			return new GuiSpawnerSettings(player, x, y, z);
		}
		else if(ID == GUI_ID_SKULL_NAME) {
			return new GuiSkull(player);
		}
		else if(ID == GUI_ID_ITEM_EDITOR) {
			return new GuiItemEditor(player, x);
		}
		else if(ID == GUI_ID_FILTERS_1) {
		    List<IFilter> filters = FilterManager.getApplicableFilters(player, world, x, y, z);
		    List<IServerFilter> serverFilters = FilterManager.getApplicableServerFilters(player, world, x, y, z);
		    if(!filters.isEmpty() && !serverFilters.isEmpty()) {
		    	return new GuiFilterMenu(player, x, y, z, filters, serverFilters);
		    }
		}
		else if(ID == GUI_ID_FILTERS_2) {
		    List<IFilter> filters = FilterManager.getApplicableEntityFilters(player, world, x);
		    List<IServerFilter> serverFilters = FilterManager.getApplicableEntityServerFilters(player, world, x);
		    if(!filters.isEmpty() && !serverFilters.isEmpty()) {
		    	return new GuiFilterMenu(player, x, filters, serverFilters);
		    }
		}
		else if(ID == GUI_ID_WATCH_PLAYER) {
			return new GuiWatchPlayer(player);
		}
		return null;
	}

	public void registerHandlers() {}

	public void onPreLoad() {
		//Blocks
		FilterManager.registerFilter(new FilterServerFillInventory());
		FilterManager.registerFilter(new FilterServerConvertToDropper());
		FilterManager.registerFilter(new FilterServerConvertToDispenser());
		FilterManager.registerFilter(new FilterServerMobType());
		FilterManager.registerFilter(new FilterServerSpawnerTimings());
		FilterManager.registerFilter(new FilterServerMobVelocity());
		FilterManager.registerFilter(new FilterServerMobPosition());
		FilterManager.registerFilter(new FilterServerMobArmor());
		FilterManager.registerFilter(new FilterServerBabyMonster());
		FilterManager.registerFilter(new FilterServerCreeperExplosion());
		FilterManager.registerFilter(new FilterServerPotionSpawner());
		FilterManager.registerFilter(new FilterServerItemSpawner());
		FilterManager.registerFilter(new FilterServerChestSymmetrify());
		FilterManager.registerFilter(new FilterServerEditSign());
		FilterManager.registerFilter(new FilterServerCommandBlockAlias());
		
		//Entities
		FilterManager.registerFilter(new FilterServerVillagerShop());
		FilterManager.registerFilter(new FilterServerMobMaxHealth());
		FilterManager.registerFilter(new FilterServerFrameDropChance());
	}
}
