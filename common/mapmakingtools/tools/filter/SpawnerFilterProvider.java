package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.FilterManager;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IFilterProvider;
import mapmakingtools.api.IFilterServer;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class SpawnerFilterProvider implements IFilterProvider {

	@Override
	public void addFilterClientToBlockList(EntityPlayer player, World world, int x, int y, int z, List<IFilterClient> filterList) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		filterList.add(FilterManager.getClientFilterFromClass(MobTypeClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(MobPositionClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(MobArmorClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(ItemSpawnerClientFilter.class));
	}

	@Override
	public void addFilterServerToBlockList(EntityPlayer player, World world, int x, int y, int z, List<IFilterServer> filterList) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
		
		filterList.add(FilterManager.getServerFilterFromClass(MobTypeServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(MobPositionServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(MobArmorServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(ItemSpawnerServerFilter.class));
	}

	@Override
	public void addFilterClientToEntityList(EntityPlayer player, Entity entity, List<IFilterClient> filterList) {
		
	}

	@Override
	public void addFilterServerToEntityList(EntityPlayer player, Entity entity, List<IFilterServer> filterList) {
		
	}

}
