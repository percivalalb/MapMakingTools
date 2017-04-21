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
	
	public SlotFake(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
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
