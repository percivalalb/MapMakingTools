package mapmakingtools.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public abstract class FilterServer extends FilterBase {

	public void addSlots(IContainerFilter container) {}
	public ItemStack transferStackInSlot(IContainerFilter container, EntityPlayer playerIn, int index) { 
		ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = (Slot)container.getInventorySlots().get(index);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	        
	        if(itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if(itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;
	    }

	    return itemstack;
	}

	public String getSaveId() { return null; }
	public void readFromNBT(NBTTagCompound tag) {}
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { return tag; }
}
