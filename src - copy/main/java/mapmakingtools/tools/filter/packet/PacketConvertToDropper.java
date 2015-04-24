package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.CustomGiveServerFilter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketConvertToDropper extends IPacket {

	public int x, y, z;
	
	public PacketConvertToDropper() {}
	public PacketConvertToDropper(int x, int y, int z) {
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
		
		Block block = player.worldObj.getBlock(this.x, this.y, this.z);
		int blockMeta = player.worldObj.getBlockMetadata(this.x, this.y, this.z);
		if(Block.isEqualTo(block, Blocks.dispenser)) {
			TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
			if(tile instanceof TileEntityDispenser) {
				TileEntityDispenser dispenser = (TileEntityDispenser)tile;
				ItemStack[] slots = new ItemStack[dispenser.getSizeInventory()];
				for(int slotCount = 0; slotCount < dispenser.getSizeInventory(); ++slotCount) {
					ItemStack stack = dispenser.getStackInSlot(slotCount);
					if(stack != null) {
						slots[slotCount] = stack.copy();
						dispenser.setInventorySlotContents(slotCount, (ItemStack)null);
					}
				}
				player.worldObj.setBlock(this.x, this.y, this.z, Blocks.dropper);
				player.worldObj.setBlockMetadataWithNotify(this.x, this.y, this.z, blockMeta, 2);
				tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
				if(tile instanceof TileEntityDropper) {
					TileEntityDropper dropper = (TileEntityDropper)tile;
					for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
						dropper.setInventorySlotContents(slotCount, slots[slotCount]);
					}
				}
			}
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.converttodropper.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}
