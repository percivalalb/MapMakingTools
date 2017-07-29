package mapmakingtools.api.manager;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.api.interfaces.IFilterProvider;
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
	
	private static final List<FilterClient> clientMap = new ArrayList<FilterClient>();
	private static final List<FilterServer> serverMap = new ArrayList<FilterServer>();
	private static final List<IFilterProvider> providerMap = new ArrayList<IFilterProvider>();
	
	public static void registerFilter(Class<? extends FilterClient> filterClient, Class<? extends FilterServer> filterServer) {
		try {
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			
			FilterServer server = filterServer.newInstance();
			
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
	
	public static List<FilterClient> getClientMap() {
		return clientMap;
	}
	
	public static List<FilterServer> getServerMap() {
		return serverMap;
	}
	
	public static List<FilterServer> getServerFiltersFromList(List<FilterClient> clientFilters) {
		List<FilterServer> serverFilters = new ArrayList<FilterServer>();
		for(FilterClient filter : clientFilters)
			serverFilters.add(filter.getServerFilter());
		return serverFilters;
	}
	
	public static List<FilterClient> getClientBlocksFilters(EntityPlayer player, World world, BlockPos pos) {
		List<FilterClient> list = new ArrayList<FilterClient>();
		for(FilterClient filter : clientMap) {
			if(filter.isApplicable(player, world, pos))
				list.add(filter);
		}
		for(IFilterProvider provider : providerMap)
			provider.addFilterClientToBlockList(player, world, pos, list);
		return list;
	}
	
	public static List<FilterServer> getServerBlocksFilters(EntityPlayer player, World world, BlockPos pos) {
		List<FilterServer> list = new ArrayList<FilterServer>();
		for(FilterServer filter : serverMap)
			if(filter.isApplicable(player, world, pos))
				list.add(filter);
		for(IFilterProvider provider : providerMap)
			provider.addFilterServerToBlockList(player, world, pos, list);
		return list;
	}
	
	public static List<FilterClient> getClientEntitiesFilters(EntityPlayer player, Entity entity) {
		List<FilterClient> list = new ArrayList<FilterClient>();
		for(FilterClient filter : clientMap)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		for(IFilterProvider provider : providerMap)
			provider.addFilterClientToEntityList(player, entity, list);
		return list;
	}
	
	public static List<FilterServer> getServerEntitiesFilters(EntityPlayer player, Entity entity) {
		List<FilterServer> list = new ArrayList<FilterServer>();
		for(FilterServer filter : serverMap)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		for(IFilterProvider provider : providerMap)
			provider.addFilterServerToEntityList(player, entity, list);
		return list;
	}

	public static FilterClient getClientFilterFromClass(Class<? extends FilterClient> class1) {
		for(FilterClient filter : clientMap)
			if(filter.getClass() == class1)
				return filter;
		return null;
	}
	
	public static FilterServer getServerFilterFromClass(Class<? extends FilterServer> class1) {
		for(FilterServer filter : serverMap)
			if(filter.getClass() == class1)
				return filter;
		return null;
	}
}
