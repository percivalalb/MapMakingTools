package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.FMLLog;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.CustomGiveServerFilter;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketCustomGive extends IPacket {

	public int x, y, z;
	
	public PacketCustomGive() {}
	public PacketCustomGive(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
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
		    				command += " " + String.valueOf(stack.stackTagCompound);
						
		    			commandBlock.func_145993_a().func_145752_a(command);
				    	
		    			if(ServerHelper.isServer()) {
			    			MinecraftServer server = MinecraftServer.getServer();
			    			server.getConfigurationManager().sendPacketToAllPlayersInDimension(commandBlock.getDescriptionPacket(), commandBlock.getWorldObj().provider.dimensionId);
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
