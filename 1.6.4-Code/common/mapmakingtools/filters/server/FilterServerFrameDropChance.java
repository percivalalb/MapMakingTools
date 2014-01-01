package mapmakingtools.filters.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.inventory.ContainerFilter;

/**
 * @author ProPercivalalb
 */
public class FilterServerFrameDropChance implements IServerFilter {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		if(entity instanceof EntityItemFrame) {
			return true;
		}
		return false;
	}

	@Override
	public void addSlots(ContainerFilter containerFilter) {
		
	}

	@Override
	public ItemStack transferStackInSlot(ContainerFilter containerFilter, EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}
}
