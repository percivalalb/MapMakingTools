package mapmakingtools.api;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FilterManager {
	
	private static final List<IFilterClient> clientMap = new ArrayList<IFilterClient>();
	private static final List<IFilterServer> serverMap = new ArrayList<IFilterServer>();
	
	public static void registerFilter(Class<? extends IFilterClient> filterClient, Class<? extends IFilterServer> filterServer) {
		try {
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			
			IFilterServer server = filterServer.newInstance();
			
			if(side == Side.CLIENT) {
				clientMap.add(filterClient.newInstance().setServerFilter(server));
				serverMap.add(server);
			}
			else
				serverMap.add(server);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<IFilterServer> getServerMap() {
		return serverMap;
	}
	
	public static void registerIcons(IIconRegister iconRegistry) {
		for(IFilterClient filter : clientMap)
			filter.registerIcons(iconRegistry);
	}
	
	public static List<IFilterServer> getServerFiltersFromList(List<IFilterClient> clientFilters) {
		List<IFilterServer> serverFilters = new ArrayList<IFilterServer>();
		for(IFilterClient filter : clientFilters)
			serverFilters.add(filter.getServerFilter());
		return serverFilters;
	}
	
	public static List<IFilterClient> getClientBlocksFilters(EntityPlayer player, World world, int x, int y, int z) {
		List<IFilterClient> list = new ArrayList<IFilterClient>();
		for(IFilterClient filter : clientMap)
			if(filter.isApplicable(player, world, x, y, z))
				list.add(filter);
		return list;
	}
	
	public static List<IFilterServer> getServerBlocksFilters(EntityPlayer player, World world, int x, int y, int z) {
		List<IFilterServer> list = new ArrayList<IFilterServer>();
		for(IFilterServer filter : serverMap)
			if(filter.isApplicable(player, world, x, y, z))
				list.add(filter);
		return list;
	}
	
	public static List<IFilterClient> getClientEntitiesFilters(EntityPlayer player, Entity entity) {
		List<IFilterClient> list = new ArrayList<IFilterClient>();
		for(IFilterClient filter : clientMap)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		return list;
	}
	
	public static List<IFilterServer> getServerEntitiesFilters(EntityPlayer player, Entity entity) {
		List<IFilterServer> list = new ArrayList<IFilterServer>();
		for(IFilterServer filter : serverMap)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		return list;
	}
}
