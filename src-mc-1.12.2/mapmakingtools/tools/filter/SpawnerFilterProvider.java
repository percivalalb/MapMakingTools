package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.api.interfaces.IFilterProvider;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.api.manager.FilterManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class SpawnerFilterProvider implements IFilterProvider {

	@Override
	public void addFilterClientToBlockList(EntityPlayer player, World world, BlockPos pos, List<FilterClient> filterList) {
		TileEntity tile = FakeWorldManager.getTileEntity(world, pos);
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		filterList.add(FilterManager.getClientFilterFromClass(MobTypeClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(SpawnerTimingClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(MobPositionClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(MobVelocityClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(BabyMonsterClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(MobArmourClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(CreeperPropertiesClientFilter.class));
		filterList.add(FilterManager.getClientFilterFromClass(ItemSpawnerClientFilter.class));
	}

	@Override
	public void addFilterServerToBlockList(EntityPlayer player, World world, BlockPos pos, List<FilterServer> filterList) {
		TileEntity tile = world.getTileEntity(pos);
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		filterList.add(FilterManager.getServerFilterFromClass(MobTypeServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(SpawnerTimingServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(MobPositionServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(MobVelocityServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(BabyMonsterServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(MobArmourServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(CreeperPropertiesServerFilter.class));
		filterList.add(FilterManager.getServerFilterFromClass(ItemSpawnerServerFilter.class));
	}

	@Override
	public void addFilterClientToEntityList(EntityPlayer player, Entity entity, List<FilterClient> filterList) {
		
	}

	@Override
	public void addFilterServerToEntityList(EntityPlayer player, Entity entity, List<FilterServer> filterList) {
		
	}

}
