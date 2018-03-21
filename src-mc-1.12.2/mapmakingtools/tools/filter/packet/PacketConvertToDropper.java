package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

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
		
		IBlockState blockState = player.world.getBlockState(this.pos);
		if(Block.isEqualTo(blockState.getBlock(), Blocks.DISPENSER)) {
			TileEntity tile = player.world.getTileEntity(this.pos);
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
				player.world.setBlockState(this.pos, Blocks.DROPPER.getDefaultState(), 3);
				tile = player.world.getTileEntity(this.pos);
				if(tile instanceof TileEntityDropper) {
					TileEntityDropper dropper = (TileEntityDropper)tile;
					for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
						dropper.setInventorySlotContents(slotCount, slots[slotCount]);
					}
				}
			}
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.converttodropper.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

}
