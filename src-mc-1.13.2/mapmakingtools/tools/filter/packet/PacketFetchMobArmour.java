package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmourServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketFetchMobArmour {

	public int minecartIndex;
	
	public PacketFetchMobArmour(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketFetchMobArmour msg, PacketBuffer buf) {
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketFetchMobArmour decode(PacketBuffer buf) {
		int minecartIndex = buf.readInt();
		return new PacketFetchMobArmour(minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketFetchMobArmour msg, Supplier<NetworkEvent.Context> ctx) {
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
        				
        					ItemStack[] mobArmor = SpawnerUtil.getMobArmor(spawnerLogic, msg.minecartIndex);
        					ItemStack[] mobHeld = SpawnerUtil.getMobHeldItems(spawnerLogic, msg.minecartIndex);
        					
        					for(int i = 0; i < mobHeld.length; ++i) {
        						inventory.setInventorySlotContents(1 - i, mobHeld[i]);
        				    }
        					for(int i = 0; i < mobArmor.length; ++i) {
        						inventory.setInventorySlotContents(i + 2, mobArmor[i]);
        				    }
        				}
        				else if(container.getTargetType() == TargetType.ENTITY) {
        					Entity entity = container.getEntity();
        					if(entity instanceof EntityLiving) {
        						EntityLiving living = (EntityLiving)container.getEntity();
        						inventory.setInventorySlotContents(5, living.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        						inventory.setInventorySlotContents(4, living.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        						inventory.setInventorySlotContents(3, living.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        						inventory.setInventorySlotContents(2, living.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        						inventory.setInventorySlotContents(1, living.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND));
        						inventory.setInventorySlotContents(0, living.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND));
        					}
        				}
        				
        				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.update");
        				chatComponent.getStyle().setItalic(true);
        				player.sendMessage(chatComponent);
        			}
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}

}
