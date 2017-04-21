package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.CustomGiveServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCustomGive extends AbstractServerMessage {
	
	public BlockPos pos;
	
	public PacketCustomGive() {}
	public PacketCustomGive(BlockPos pos) {
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
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof CustomGiveServerFilter) {
				
				CustomGiveServerFilter filterCurrent = (CustomGiveServerFilter)container.filterCurrent;
				if (tile instanceof TileEntityCommandBlock) {
					TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
					
					
		    		if(container.getSlot(0).getStack() != null) {
		    			String command = "/give @p";
		    			ItemStack stack = container.getSlot(0).getStack().copy();
		    			command += " " + Item.itemRegistry.getNameForObject(stack.getItem());
		    			command += " " + stack.stackSize;
		    			command += " " + stack.getItemDamage();
		    			
		    			if(stack.hasTagCompound())
		    				command += " " + String.valueOf(stack.getTagCompound());
						
		    			commandBlock.getCommandBlockLogic().setCommand(command);
				    	
		    			if(ServerHelper.isServer()) {
			    			MinecraftServer server = MinecraftServer.getServer();
			    			server.getConfigurationManager().sendPacketToAllPlayersInDimension(commandBlock.getDescriptionPacket(), commandBlock.getWorld().provider.getDimensionId());
		    			}
		    			
		    			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.customgive.complete", command);
						chatComponent.getChatStyle().setItalic(true);
						player.addChatMessage(chatComponent);
		    		}
				}
			}
		}
	}

}
