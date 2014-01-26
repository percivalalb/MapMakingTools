package mapmakingtools.tools.filter;

import java.util.List;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import mapmakingtools.api.FilterManager;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IFilterProvider;
import mapmakingtools.api.IFilterServer;
import mapmakingtools.util.SpawnerUtil;

/**
 * @author ProPercivalalb
 */
public class SpawnerFilterProvider implements IFilterProvider {

	@Override
	public void addFilterClientToBlockList(EntityPlayer player, World world, int x, int y, int z, List<IFilterClient> filterList) {
		TileEntity tile = world.func_147438_o(x, y, z);
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		for(WeightedRandomMinecart randomMinecart : SpawnerUtil.getRandomMinecarts(spawner.func_145881_a())) {
			filterList.add(FilterManager.getClientFilterFromClass(MobTypeClientFilter.class));
			filterList.add(FilterManager.getClientFilterFromClass(MobArmorClientFilter.class));
			FMLLog.info("Client cart  " + randomMinecart.minecartName + " " + randomMinecart);
		}
	}

	@Override
	public void addFilterServerToBlockList(EntityPlayer player, World world, int x, int y, int z, List<IFilterServer> filterList) {
		TileEntity tile = world.func_147438_o(x, y, z);
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		SpawnerUtil.confirmHasRandomMinecart(spawner.func_145881_a());
		SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
		
		for(WeightedRandomMinecart randomMinecart : SpawnerUtil.getRandomMinecarts(spawner.func_145881_a())) {
			filterList.add(FilterManager.getServerFilterFromClass(MobTypeServerFilter.class));
			filterList.add(FilterManager.getServerFilterFromClass(MobArmorServerFilter.class));
			FMLLog.info("Server cart  " + randomMinecart.minecartName + " " + randomMinecart);
		}
	}

	@Override
	public void addFilterClientToEntityList(EntityPlayer player, Entity entity, List<IFilterClient> filterList) {
		
	}

	@Override
	public void addFilterServerToEntityList(EntityPlayer player, Entity entity, List<IFilterServer> filterList) {
		
	}

}
