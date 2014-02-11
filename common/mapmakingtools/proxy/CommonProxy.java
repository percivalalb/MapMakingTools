package mapmakingtools.proxy;

import java.util.List;

import mapmakingtools.api.FilterManager;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IFilterServer;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.tools.filter.FillInventoryClientFilter;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import mapmakingtools.tools.filter.MobArmorClientFilter;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.tools.filter.MobTypeClientFilter;
import mapmakingtools.tools.filter.MobTypeServerFilter;
import mapmakingtools.tools.filter.SpawnerFilterProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * @author ProPercivalalb
 */
public class CommonProxy implements IGuiHandler {

	public static final int ID_FILTER_BLOCK = 0;
	public static final int ID_FILTER_ENTITY = 1;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == ID_FILTER_BLOCK) {
			List<IFilterServer> filterList = FilterManager.getServerBlocksFilters(player, world, x, y, z);
			if(filterList.size() > 0)
				return new ContainerFilter(filterList, player).setBlockCoords(x, y, z);
		}
		else if(ID == ID_FILTER_ENTITY) {
			List<IFilterServer> filterList = FilterManager.getServerEntitiesFilters(player, world.getEntityByID(x));
			if(filterList.size() > 0)
				return new ContainerFilter(filterList, player).setEntityId(x);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) { 
		if(ID == ID_FILTER_BLOCK) {
			List<IFilterClient> filterList = FilterManager.getClientBlocksFilters(player, world, x, y, z);
			if(filterList.size() > 0)
				return new GuiFilter(filterList, player, x, y, z);
		}
		else if(ID == ID_FILTER_ENTITY) {
			List<IFilterClient> filterList = FilterManager.getClientEntitiesFilters(player, world.getEntityByID(x));
			if(filterList.size() > 0)
				return new GuiFilter(filterList, player, x);
		}
		return null;
	}

	public void registerHandlers() {}
	public void onPreLoad() {}
	public EntityPlayer getClientPlayer() { return null; }
	
	
	public void registerFilters() {
    	FilterManager.registerFilter(FillInventoryClientFilter.class, FillInventoryServerFilter.class);
    	FilterManager.registerFilter(MobTypeClientFilter.class, MobTypeServerFilter.class);
    	FilterManager.registerFilter(MobArmorClientFilter.class, MobArmorServerFilter.class);
    	
    	FilterManager.registerProvider(SpawnerFilterProvider.class);
	}
}
