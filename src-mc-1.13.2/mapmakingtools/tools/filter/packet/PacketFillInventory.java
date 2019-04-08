package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketFillInventory {

	public BlockPos pos;
	
	public PacketFillInventory(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketFillInventory msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
	}
	
	public static PacketFillInventory decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		return new PacketFillInventory(pos);
	}
	
	public static class Handler {
        public static void handle(final PacketFillInventory msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		TileEntity tile = player.world.getTileEntity(msg.pos);
        		if(player.openContainer instanceof ContainerFilter) {
        			
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			if(container.filterCurrent instanceof FillInventoryServerFilter) {
        				
        				FillInventoryServerFilter filterCurrent = (FillInventoryServerFilter)container.filterCurrent;
        				if (tile instanceof IInventory) {
        					
        			    	for(int i = 0; i < ((IInventory)tile).getSizeInventory(); ++i) {

        			    		((IInventory)tile).setInventorySlotContents(i, container.getSlot(0).getStack().copy());
        			    	}
        			    	
        					player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.fillinventory.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName()).applyTextStyle(TextFormatting.ITALIC));
        				}
        			}
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
