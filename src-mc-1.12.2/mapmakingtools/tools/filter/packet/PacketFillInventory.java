package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketFillInventory extends AbstractServerMessage {

	public BlockPos pos;
	
	public PacketFillInventory() {}
	public PacketFillInventory(BlockPos pos) {
		this.pos = pos;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof FillInventoryServerFilter) {
				
				FillInventoryServerFilter filterCurrent = (FillInventoryServerFilter)container.filterCurrent;
				if (tile instanceof IInventory) {
					
			    	for(int i = 0; i < ((IInventory)tile).getSizeInventory(); ++i) {

			    		((IInventory)tile).setInventorySlotContents(i, container.getSlot(0).getStack().copy());
			    	}
			    	TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.fillinventory.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
		}
	}

}
