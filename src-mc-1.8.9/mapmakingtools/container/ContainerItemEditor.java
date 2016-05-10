package mapmakingtools.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class ContainerItemEditor extends Container {
	
	private int slotIndex;
	
	public ContainerItemEditor(EntityPlayer player, int slotIndex) {
		this.slotIndex = slotIndex;
		byte b0 = 51;
		int i;

		
		this.addSlotToContainer(new Slot(player.inventory, slotIndex, 10, 10) {
			@Override 
			public boolean isItemValid(ItemStack stack) {
			    return false;
			}
			
			@Override 
			public boolean canTakeStack(EntityPlayer player) {
		        return false;
		    }
		});
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.inventory.isUseableByPlayer(player);
	}
}