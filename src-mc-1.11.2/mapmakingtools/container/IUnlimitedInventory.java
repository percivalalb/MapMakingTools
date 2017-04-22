package mapmakingtools.container;

import net.minecraft.inventory.InventoryBasic;

/**
 * @author ProPercivalalb
 */
public abstract class IUnlimitedInventory extends InventoryBasic {

	public IUnlimitedInventory(String title, boolean customName, int slotCount) {
		super(title, customName, slotCount);
	}
	
	public abstract boolean isSlotUnlimited(int slotIndex);
	public abstract void setSlotUnlimited(int slotIndex, boolean isUnlimited);
}
