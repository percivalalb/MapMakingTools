package mapmakingtools.api.manager;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IFilterProvider;
import mapmakingtools.api.interfaces.IFilterServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class FilterManager {
	
	private static final List<IFilterClient> clientMap = new ArrayList<IFilterClient>();
	private static final List<IFilterServer> serverMap = new ArrayList<IFilterServer>();
	private static final List<IFilterProvider> providerMap = new ArrayList<IFilterProvider>();
	
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
	
	public static void registerProvider(Class<? extends IFilterProvider> filterProvider) {
		try {
			providerMap.add(filterProvider.newInstance());
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<IFilterClient> getClientMap() {
		return clientMap;
	}
	
	public static List<IFilterServer> getServerMap() {
		return serverMap;
	}
	
	public static List<IFilterServer> getServerFiltersFromList(List<IFilterClient> clientFilters) {
		List<IFilterServer> serverFilters = new ArrayList<IFilterServer>();
		for(IFilterClient filter : clientFilters)
			serverFilters.add(filter.getServerFilter());
		return serverFilters;
	}
	
	public static List<IFilterClient> getClientBlocksFilters(EntityPlayer player, World world, BlockPos pos) {
		List<IFilterClient> list = new ArrayList<IFilterClient>();
		for(IFilterClient filter : clientMap) {
			if(filter.isApplicable(player, world, pos))
				list.add(filter);
		}
		for(IFilterProvider provider : providerMap)
			provider.addFilterClientToBlockList(player, world, pos, list);
		return list;
	}
	
	public static List<IFilterServer> getServerBlocksFilters(EntityPlayer player, World world, BlockPos pos) {
		List<IFilterServer> list = new ArrayList<IFilterServer>();
		for(IFilterServer filter : serverMap)
			if(filter.isApplicable(player, world, pos))
				list.add(filter);
		for(IFilterProvider provider : providerMap)
			provider.addFilterServerToBlockList(player, world, pos, list);
		return list;
	}
	
	public static List<IFilterClient> getClientEntitiesFilters(EntityPlayer player, Entity entity) {
		List<IFilterClient> list = new ArrayList<IFilterClient>();
		for(IFilterClient filter : clientMap)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		for(IFilterProvider provider : providerMap)
			provider.addFilterClientToEntityList(player, entity, list);
		return list;
	}
	
	public static List<IFilterServer> getServerEntitiesFilters(EntityPlayer player, Entity entity) {
		List<IFilterServer> list = new ArrayList<IFilterServer>();
		for(IFilterServer filter : serverMap)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		for(IFilterProvider provider : providerMap)
			provider.addFilterServerToEntityList(player, entity, list);
		return list;
	}

	public static IFilterClient getClientFilterFromClass(Class<? extends IFilterClient> class1) {
		for(IFilterClient filter : clientMap)
			if(filter.getClass() == class1)
				return filter;
		return null;
	}
	
	public static IFilterServer getServerFilterFromClass(Class<? extends IFilterServer> class1) {
		for(IFilterServer filter : serverMap)
			if(filter.getClass() == class1)
				return filter;
		return null;
	}
}
