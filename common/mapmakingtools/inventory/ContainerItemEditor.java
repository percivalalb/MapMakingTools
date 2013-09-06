package mapmakingtools.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;

/**
 * @author ProPercivalalb
 */
public class ContainerItemEditor extends Container {
	
	private int slotNo;
	
	public ContainerItemEditor(EntityPlayer player, int slotNo) {
		this.slotNo = slotNo;
		byte b0 = 51;
		int i;

		this.addSlotToContainer(new Slot(player.inventory, slotNo, 9, 9) {
			@Override 
			public boolean isItemValid(ItemStack stack) {
			    return false;
			}
			
			@Override 
			public boolean canTakeStack(EntityPlayer player) {
		        return false;
		    }
		});
		
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                //this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
            }
        }

        for (i = 0; i < 9; ++i) {
            //this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 58 + b0));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(player != null && player.inventory.getStackInSlot(slotNo) != null) {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("itemeditor.closeNoItem", new Object[0]));
			return false;
		}
		return true;
	}
}