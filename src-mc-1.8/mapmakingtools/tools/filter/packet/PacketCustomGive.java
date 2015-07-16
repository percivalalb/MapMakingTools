package mapmakingtools.tools.filter.packet;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.CustomGiveServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketCustomGive extends IPacketPos {
	
	public PacketCustomGive() {}
	public PacketCustomGive(BlockPos pos) {
		super(pos);
	}

	@Override
	public void execute(EntityPlayer player) {
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
