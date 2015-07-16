package mapmakingtools.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public abstract class IFilterServer extends IFilter {

	public void addSlots(IContainerFilter container) {}
	public ItemStack transferStackInSlot(IContainerFilter container, EntityPlayer par1EntityPlayer, int par2) { return null; }

	public String getSaveId() { return null; }
	public void readFromNBT(NBTTagCompound tag) {}
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { return tag; }
}
