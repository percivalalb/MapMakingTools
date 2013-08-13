package mapmakingtools.core.proxy;

import java.util.List;

import mapmakingtools.api.FilterRegistry;
import mapmakingtools.api.IFilter;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.client.gui.GuiSkull;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.util.WrenchTasks;
import mapmakingtools.filters.server.FilterServerChestSymmetrify;
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
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == GUI_ID_SPAWNER_SETTING) {} //Do nothing on server
		else if(ID == GUI_ID_SKULL_NAME) {} //Do nothing on server
		else if(ID == GUI_ID_ITEM_EDITOR) {
			return new ContainerItemEditor(player);
		}
		else if(ID == GUI_ID_FILTERS_1) {
		    List<IServerFilter> filters = FilterRegistry.getApplicableServerFilters(player, world, x, y, z);
		    if(!filters.isEmpty()) {
		    	WrenchTasks.addTaskBlock(player, world, x, y, z);
		    	return new ContainerFilter(player, filters).setCor(x, y, z);
		    }
		}
		else if(ID == GUI_ID_FILTERS_2) {
		    List<IServerFilter> filters = FilterRegistry.getApplicableEntityServerFilters(player, world, x);
		    if(!filters.isEmpty()) {
		    	WrenchTasks.addTaskEntity(player, world, x);
		    	return new ContainerFilter(player, filters).setEntityId(x);
		    }
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
			return new GuiItemEditor(player);
		}
		else if(ID == GUI_ID_FILTERS_1) {
		    List<IFilter> filters = FilterRegistry.getApplicableFilters(player, world, x, y, z);
		    List<IServerFilter> serverFilters = FilterRegistry.getApplicableServerFilters(player, world, x, y, z);
		    if(!filters.isEmpty() && !serverFilters.isEmpty()) {
		    	return new GuiFilterMenu(player, x, y, z, filters, serverFilters);
		    }
		}
		else if(ID == GUI_ID_FILTERS_2) {
		    List<IFilter> filters = FilterRegistry.getApplicableEntityFilters(player, world, x);
		    List<IServerFilter> serverFilters = FilterRegistry.getApplicableEntityServerFilters(player, world, x);
		    if(!filters.isEmpty() && !serverFilters.isEmpty()) {
		    	return new GuiFilterMenu(player, x, filters, serverFilters);
		    }
		}
		return null;
	}

	public void registerHandlers() {}

	public void onPreLoad() {
		//Blocks
		FilterRegistry.registerFilter(new FilterServerFillInventory());
		FilterRegistry.registerFilter(new FilterServerConvertToDropper());
		FilterRegistry.registerFilter(new FilterServerConvertToDispenser());
		FilterRegistry.registerFilter(new FilterServerMobType());
		FilterRegistry.registerFilter(new FilterServerSpawnerTimings());
		FilterRegistry.registerFilter(new FilterServerMobVelocity());
		FilterRegistry.registerFilter(new FilterServerMobPosition());
		FilterRegistry.registerFilter(new FilterServerMobArmor());
		FilterRegistry.registerFilter(new FilterServerBabyMonster());
		FilterRegistry.registerFilter(new FilterServerCreeperExplosion());
		FilterRegistry.registerFilter(new FilterServerPotionSpawner());
		FilterRegistry.registerFilter(new FilterServerItemSpawner());
		FilterRegistry.registerFilter(new FilterServerChestSymmetrify());
		
		//Entities
		FilterRegistry.registerFilter(new FilterServerVillagerShop());
	}
}
