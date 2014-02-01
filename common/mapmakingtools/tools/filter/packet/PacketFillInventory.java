package mapmakingtools.tools.filter.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.network.packet.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class PacketFillInventory extends IPacket {

	public int x, y, z;
	
	public PacketFillInventory() {}
	public PacketFillInventory(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		this.x = bytes.readInt();
		this.y = bytes.readInt();
		this.z = bytes.readInt();
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		bytes.writeInt(x);
		bytes.writeInt(y);
		bytes.writeInt(z);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.func_147438_o(x, y, z);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof FillInventoryServerFilter) {
				
				FillInventoryServerFilter filterCurrent = (FillInventoryServerFilter)container.filterCurrent;
				if (tile instanceof IInventory) {
					
			    	for(int var8 = 0; var8 < ((IInventory)tile).getSizeInventory(); ++var8) {
			    		ItemStack newStack = null;
			    		if(container.getSlot(0).getStack() != null) {
			    			newStack = container.getSlot(0).getStack().copy();
			    			newStack.stackSize = ((IPhantomSlot)container.getSlot(0)).isUnlimited() ? -1 : container.getSlot(0).getStack().stackSize;
			    		}
			    		((IInventory)tile).setInventorySlotContents(var8, newStack);
			    	}
			    	ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.fillInventory.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
					player.func_145747_a(chatComponent);
				}
			}
		}
	}

}
