package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.ItemSpawnerServerFilter;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketItemSpawner {

	public int minecartIndex;
	
	public PacketItemSpawner(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketItemSpawner msg, PacketBuffer buf) {
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketItemSpawner decode(PacketBuffer buf) {
		int minecartIndex = buf.readInt();
		return new PacketItemSpawner(minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketItemSpawner msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			if(container.filterCurrent instanceof ItemSpawnerServerFilter) {
        				ItemSpawnerServerFilter filterCurrent = (ItemSpawnerServerFilter)container.filterCurrent;
        				IInventory inventory = filterCurrent.getInventory(container); 
        				
        				if(SpawnerUtil.isSpawner(container)) {
        					MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        				
        					ItemStack item = inventory.getStackInSlot(0).copy();
        					SpawnerUtil.setItemType(spawnerLogic, item, msg.minecartIndex);
        					
        					if(container.getTargetType() == TargetType.BLOCK) {
        						TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
        		
        						//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        						PacketUtil.sendTileEntityUpdateToWatching(spawner);
        					}
        					
        					
        					player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.changeitem.complete", item.isEmpty() ? "Nothing" : item.getDisplayName()).applyTextStyle(TextFormatting.ITALIC));
        				}
        			}
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
