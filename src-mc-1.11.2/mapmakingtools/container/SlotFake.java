package mapmakingtools.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class SlotFake extends Slot implements IPhantomSlot {

	public boolean canBeUnlimited = true;
	public boolean isUnlimited = false;
	
	public SlotFake(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean canAdjust() {
		return true;
	}

	@Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return false;
    }

	@Override
	public void setIsUnlimited(boolean isUnlimited) {
		if(this.inventory instanceof IUnlimitedInventory)
			((IUnlimitedInventory)this.inventory).setSlotUnlimited(this.getSlotIndex(), isUnlimited);
		this.isUnlimited = isUnlimited;
	}

	@Override
	public boolean isUnlimited() {
		if(this.inventory instanceof IUnlimitedInventory)
			return ((IUnlimitedInventory)this.inventory).isSlotUnlimited(this.getSlotIndex());
		return this.isUnlimited;
	}

	@Override
	public boolean canBeUnlimited() {
		return this.canBeUnlimited;
	}
	
	public SlotFake setCantBeUnlimited() {
		this.canBeUnlimited = false;
		return this;
	}
}
