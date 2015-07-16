package mapmakingtools.tools.filter.packet;

import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketConvertToDispenser extends IPacketPos {
	
	public PacketConvertToDispenser() {}
	public PacketConvertToDispenser(BlockPos pos) {
		super(pos);
	}
	
	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		Block block = player.worldObj.getBlock(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		int blockMeta = player.worldObj.getBlockMetadata(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(Block.isEqualTo(block, Blocks.dropper)) {
			TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
			if(tile instanceof TileEntityDropper) {
				TileEntityDropper dropper = (TileEntityDropper)tile;
				ItemStack[] slots = new ItemStack[dropper.getSizeInventory()];
				for(int slotCount = 0; slotCount < dropper.getSizeInventory(); ++slotCount) {
					ItemStack stack = dropper.getStackInSlot(slotCount);
					if(stack != null) {
						slots[slotCount] = stack.copy();
						dropper.setInventorySlotContents(slotCount, (ItemStack)null);
					}
				}
				player.worldObj.setBlock(this.pos.getX(), this.pos.getY(), this.pos.getZ(), Blocks.dispenser);
				player.worldObj.setBlockMetadataWithNotify(this.pos.getX(), this.pos.getY(), this.pos.getZ(), blockMeta, 2);
				tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
				if(tile instanceof TileEntityDispenser) {
					TileEntityDispenser dispenser = (TileEntityDispenser)tile;
					for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
						dispenser.setInventorySlotContents(slotCount, slots[slotCount]);
					}
				}
			}
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.converttodispenser.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}
