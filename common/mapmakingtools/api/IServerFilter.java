package mapmakingtools.api;

import mapmakingtools.inventory.ContainerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IServerFilter {

	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z);
	
	public boolean isApplicable(Entity entity);

	public void addSlots(ContainerFilter containerFilter);
	
	public ItemStack transferStackInSlot(ContainerFilter containerFilter, EntityPlayer par1EntityPlayer, int par2);
}
