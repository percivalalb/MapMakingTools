package mapmakingtools.tools.filter.packet;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketFillInventory extends IPacketPos {

	public PacketFillInventory() {}
	public PacketFillInventory(BlockPos pos) {
		super(pos);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof FillInventoryServerFilter) {
				
				FillInventoryServerFilter filterCurrent = (FillInventoryServerFilter)container.filterCurrent;
				if (tile instanceof IInventory) {
					
			    	for(int i = 0; i < ((IInventory)tile).getSizeInventory(); ++i) {
			    		ItemStack newStack = null;
			    		if(container.getSlot(0).getStack() != null) {
			    			newStack = container.getSlot(0).getStack().copy();
			    			newStack.stackSize = ((IPhantomSlot)container.getSlot(0)).isUnlimited() ? -1 : container.getSlot(0).getStack().stackSize;
			    		}
			    		((IInventory)tile).setInventorySlotContents(i, newStack);
			    	}
			    	ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.fillinventory.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
				}
			}
		}
	}

}
