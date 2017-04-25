package mapmakingtools.container;

import net.minecraft.inventory.InventoryBasic;

/**
 * @author ProPercivalalb
 */
public class InventoryUnlimited extends InventoryBasic {

	public boolean[] umlimited;
	
	public InventoryUnlimited(String title, boolean customName, int slotCount) {
		super(title, customName, slotCount);
		this.umlimited = new boolean[slotCount];
	}
	
	public boolean isSlotUnlimited(int slotIndex) {
		return this.umlimited[slotIndex];
	}
	
	public void setSlotUnlimited(int slotIndex, boolean isUnlimited) {
		this.umlimited[slotIndex] = isUnlimited;
	}
}
