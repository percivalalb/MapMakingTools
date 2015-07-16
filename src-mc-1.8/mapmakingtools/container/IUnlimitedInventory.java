package mapmakingtools.container;

import net.minecraft.inventory.IInventory;

/**
 * @author ProPercivalalb
 */
public interface IUnlimitedInventory extends IInventory {

	public boolean isSlotUnlimited(int slotIndex);
	public void setSlotUnlimited(int slotIndex, boolean isUnlimited);
}
