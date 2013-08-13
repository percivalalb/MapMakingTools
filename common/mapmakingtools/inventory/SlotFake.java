package mapmakingtools.inventory;

import mapmakingtools.api.IPhantomSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotFake extends Slot implements IPhantomSlot {

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
}
