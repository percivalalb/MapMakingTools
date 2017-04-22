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
public class PacketConvertToDispenser extends AbstractServerMessage {
	
	public BlockPos pos;
	
	public PacketConvertToDispenser() {}
	public PacketConvertToDispenser(BlockPos pos) {
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
		
		if(Block.isEqualTo(blockState.getBlock(), Blocks.DROPPER)) {
			TileEntity tile = player.world.getTileEntity(this.pos);
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
				player.world.setBlockState(this.pos, Blocks.DISPENSER.getDefaultState(), 3);
				tile = player.world.getTileEntity(this.pos);
				if(tile instanceof TileEntityDispenser) {
					TileEntityDispenser dispenser = (TileEntityDispenser)tile;
					for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
						dispenser.setInventorySlotContents(slotCount, slots[slotCount]);
					}
				}
			}
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.converttodispenser.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

}
