package mapmakingtools.api.manager;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.FilterServer;
import mapmakingtools.api.filter.IFilterProvider;
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
	
	/** Should be a client and server entry for each filter **/
	private static final List<FilterClient> CLIENT_FILTERS = new ArrayList<>();
	private static final List<FilterServer> SERVER_FILTERS = new ArrayList<>();
	
	private static final List<IFilterProvider> FILTER_PROVIDER = new ArrayList<>();
	
	public static void registerFilter(Class<? extends FilterClient> filterClient, Class<? extends FilterServer> filterServer) {
		try {
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			
			FilterServer server = filterServer.newInstance();
			
			SERVER_FILTERS.add(server);
			
			if(side == Side.CLIENT) {
				CLIENT_FILTERS.add(filterClient.newInstance().setServerFilter(server));
			}
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void registerProvider(Class<? extends IFilterProvider> filterProvider) {
		try {
			FILTER_PROVIDER.add(filterProvider.newInstance());
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<FilterClient> getClientMap() {
		return CLIENT_FILTERS;
	}
	
	public static List<FilterServer> getServerMap() {
		return SERVER_FILTERS;
	}
	
	public static List<FilterServer> getServerFiltersFromList(List<FilterClient> clientFilters) {
		List<FilterServer> serverFilters = new ArrayList<FilterServer>();
		for(FilterClient filter : clientFilters)
			serverFilters.add(filter.getServerFilter());
		return serverFilters;
	}
	
	public static List<FilterClient> getClientBlocksFilters(EntityPlayer player, World world, BlockPos pos) {
		List<FilterClient> list = new ArrayList<FilterClient>();
		for(FilterClient filter : CLIENT_FILTERS) {
			if(filter.isApplicable(player, world, pos))
				list.add(filter);
		}
		for(IFilterProvider provider : FILTER_PROVIDER)
			provider.addFilterClientToBlockList(player, world, pos, list);
		return list;
	}
	
	public static List<FilterServer> getServerBlocksFilters(EntityPlayer player, World world, BlockPos pos) {
		List<FilterServer> list = new ArrayList<FilterServer>();
		for(FilterServer filter : SERVER_FILTERS)
			if(filter.isApplicable(player, world, pos))
				list.add(filter);
		for(IFilterProvider provider : FILTER_PROVIDER)
			provider.addFilterServerToBlockList(player, world, pos, list);
		return list;
	}
	
	public static List<FilterClient> getClientEntitiesFilters(EntityPlayer player, Entity entity) {
		List<FilterClient> list = new ArrayList<FilterClient>();
		for(FilterClient filter : CLIENT_FILTERS)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		for(IFilterProvider provider : FILTER_PROVIDER)
			provider.addFilterClientToEntityList(player, entity, list);
		return list;
	}
	
	public static List<FilterServer> getServerEntitiesFilters(EntityPlayer player, Entity entity) {
		List<FilterServer> list = new ArrayList<FilterServer>();
		for(FilterServer filter : SERVER_FILTERS)
			if(filter.isApplicable(player, entity))
				list.add(filter);
		for(IFilterProvider provider : FILTER_PROVIDER)
			provider.addFilterServerToEntityList(player, entity, list);
		return list;
	}

	public static FilterClient getClientFilterFromClass(Class<? extends FilterClient> class1) {
		for(FilterClient filter : CLIENT_FILTERS)
			if(filter.getClass() == class1)
				return filter;
		return null;
	}
	
	public static FilterServer getServerFilterFromClass(Class<? extends FilterServer> class1) {
		for(FilterServer filter : SERVER_FILTERS)
			if(filter.getClass() == class1)
				return filter;
		return null;
	}
}
