package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmourServerFilter;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmour {

	public int minecartIndex;
	
	public PacketMobArmour(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketMobArmour msg, PacketBuffer buf) {
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketMobArmour decode(PacketBuffer buf) {
		int minecartIndex = buf.readInt();
		return new PacketMobArmour(minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketMobArmour msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			if(container.filterCurrent instanceof MobArmourServerFilter) {
        				MobArmourServerFilter filterCurrent = (MobArmourServerFilter)container.filterCurrent;
        				IInventory inventory = filterCurrent.getInventory(container); 
        				
        				if(SpawnerUtil.isSpawner(container)) {
        					MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        				
        					SpawnerUtil.setMobArmor(spawnerLogic, inventory.getStackInSlot(5), inventory.getStackInSlot(4), inventory.getStackInSlot(3), inventory.getStackInSlot(2), inventory.getStackInSlot(1), inventory.getStackInSlot(0), msg.minecartIndex);
        					
        					if(container.getTargetType() == TargetType.BLOCK) {
        						TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
        		
        						//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        						PacketUtil.sendTileEntityUpdateToWatching(spawner);
        					}
        				}
        				else if(container.getTargetType() == TargetType.ENTITY) {
        					Entity entity = container.getEntity();
        					if(entity instanceof EntityLiving) {
        						EntityLiving living = (EntityLiving)container.getEntity();
        						living.setItemStackToSlot(EntityEquipmentSlot.HEAD, inventory.getStackInSlot(5));
        						living.setItemStackToSlot(EntityEquipmentSlot.CHEST, inventory.getStackInSlot(4));
        						living.setItemStackToSlot(EntityEquipmentSlot.LEGS, inventory.getStackInSlot(3));
        						living.setItemStackToSlot(EntityEquipmentSlot.FEET, inventory.getStackInSlot(2));
        						living.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inventory.getStackInSlot(1));
        						living.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, inventory.getStackInSlot(0));
        					}
        				}
        				
        				player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.mobArmor.complete").applyTextStyle(TextFormatting.ITALIC));
        			}
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
