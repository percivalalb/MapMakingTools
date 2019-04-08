package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketConvertToDropper {

	public BlockPos pos;
	
	public PacketConvertToDropper(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketConvertToDropper msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
	}
	
	public static PacketConvertToDropper decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		return new PacketConvertToDropper(pos);
	}
	
	public static class Handler {
        public static void handle(final PacketConvertToDropper msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		IBlockState blockState = player.world.getBlockState(msg.pos);
        		if(blockState.getBlock().equals(Blocks.DISPENSER)) {
        			TileEntity tile = player.world.getTileEntity(msg.pos);
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
        				player.world.setBlockState(msg.pos, Blocks.DROPPER.getDefaultState(), 3);
        				tile = player.world.getTileEntity(msg.pos);
        				if(tile instanceof TileEntityDropper) {
        					TileEntityDropper dropper = (TileEntityDropper)tile;
        					for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
        						dropper.setInventorySlotContents(slotCount, slots[slotCount]);
        					}
        				}
        			}
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.converttodropper.complete").applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
