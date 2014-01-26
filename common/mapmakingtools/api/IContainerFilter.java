package mapmakingtools.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public interface IContainerFilter {

	public void addSlot(Slot slot);
	public EntityPlayer getPlayer();
	public List getInventorySlots();
	public boolean mergeItemStacks(ItemStack itemstack1, int j, int i, boolean b);

}
