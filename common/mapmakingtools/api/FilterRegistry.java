package mapmakingtools.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FilterRegistry {
	
	private static List<IFilter> map = new ArrayList<IFilter>();
	private static List<IServerFilter> serverMap = new ArrayList<IServerFilter>();
	
	public static void registerFilter(IFilter handler) {
		map.add(handler);
	}
	
	public static void registerFilter(IServerFilter handler) {
		serverMap.add(handler);
	}
	
	public static void registerIcons(IconRegister iconRegistry) {
		for(IFilter filter : map) {
			filter.registerIcons(iconRegistry);
		}
	}
	
	public static List<IFilter> getApplicableFilters(EntityPlayer player, World world, int x, int y, int z) {
		List<IFilter> list = new ArrayList<IFilter>();
		for(IFilter filter : map) {
			if(filter.isApplicable(player, world, x, y, z)) {
				list.add(filter);
			}
		}
		return list;
	}
	
	public static List<IServerFilter> getApplicableServerFilters(EntityPlayer player, World world, int x, int y, int z) {
		List<IServerFilter> list = new ArrayList<IServerFilter>();
		for(IServerFilter filter : serverMap) {
			if(filter.isApplicable(player, world, x, y, z)) {
				list.add(filter);
			}
		}
		return list;
	}
	
	public static List<IFilter> getApplicableEntityFilters(EntityPlayer player, World world, int entityId) {
		List<IFilter> list = new ArrayList<IFilter>();
		for(IFilter filter : map) {
			if(filter.isApplicable(world.getEntityByID(entityId))) {
				list.add(filter);
			}
		}
		return list;
	}
	
	public static List<IServerFilter> getApplicableEntityServerFilters(EntityPlayer player, World world, int entityId) {
		List<IServerFilter> list = new ArrayList<IServerFilter>();
		for(IServerFilter filter : serverMap) {
			if(filter.isApplicable(world.getEntityByID(entityId))) {
				list.add(filter);
			}
		}
		return list;
	}
}
