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
public class PacketConvertToDispenser {
	
	public BlockPos pos;
	
	public PacketConvertToDispenser(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(PacketConvertToDispenser msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
	}
	
	public static PacketConvertToDispenser decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		return new PacketConvertToDispenser(pos);
	}
	
	public static class Handler {
        public static void handle(final PacketConvertToDispenser msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		IBlockState blockState = player.world.getBlockState(msg.pos);
        		
        		if(blockState.getBlock().equals(Blocks.DROPPER)) {
        			TileEntity tile = player.world.getTileEntity(msg.pos);
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
        				player.world.setBlockState(msg.pos, Blocks.DISPENSER.getDefaultState(), 3);
        				tile = player.world.getTileEntity(msg.pos);
        				if(tile instanceof TileEntityDispenser) {
        					TileEntityDispenser dispenser = (TileEntityDispenser)tile;
        					for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
        						dispenser.setInventorySlotContents(slotCount, slots[slotCount]);
        					}
        				}
        			}
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.converttodispenser.complete").applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
