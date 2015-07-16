package mapmakingtools.tools.filter.packet;

import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketConvertToDropper extends IPacketPos {

	public PacketConvertToDropper() {}
	public PacketConvertToDropper(BlockPos pos) {
		super(pos);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		IBlockState blockState = player.worldObj.getBlockState(this.pos);
		if(Block.isEqualTo(blockState.getBlock(), Blocks.dispenser)) {
			TileEntity tile = player.worldObj.getTileEntity(this.pos);
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
				player.worldObj.setBlockState(this.pos, Blocks.dropper.getDefaultState(), 3);
				tile = player.worldObj.getTileEntity(this.pos);
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
