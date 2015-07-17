package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketConvertToDropper extends AbstractServerMessage {

	public BlockPos pos;
	
	public PacketConvertToDropper() {}
	public PacketConvertToDropper(BlockPos pos) {
		this.pos = pos;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		Block block = player.worldObj.getBlock(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		int blockMeta = player.worldObj.getBlockMetadata(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(Block.isEqualTo(block, Blocks.dispenser)) {
			TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
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
				player.worldObj.setBlock(this.pos.getX(), this.pos.getY(), this.pos.getZ(), Blocks.dropper);
				player.worldObj.setBlockMetadataWithNotify(this.pos.getX(), this.pos.getY(), this.pos.getZ(), blockMeta, 2);
				tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
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
		return null;
	}

}
