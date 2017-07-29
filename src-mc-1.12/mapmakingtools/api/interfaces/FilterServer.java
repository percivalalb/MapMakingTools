package mapmakingtools.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public abstract class FilterServer extends FilterBase {

	public void addSlots(IContainerFilter container) {}
	public ItemStack transferStackInSlot(IContainerFilter container, EntityPlayer playerIn, int index) { return null; }

	public String getSaveId() { return null; }
	public void readFromNBT(NBTTagCompound tag) {}
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { return tag; }
}
