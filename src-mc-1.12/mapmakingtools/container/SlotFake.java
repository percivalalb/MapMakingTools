package mapmakingtools.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class SlotFake extends Slot implements IPhantomSlot {
	
	public SlotFake(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean canAdjust() {
		return true;
	}

	@Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
}
