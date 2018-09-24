package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmourServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketFetchMobArmour extends AbstractServerMessage {

	public int minecartIndex;
	
	public PacketFetchMobArmour() {}
	public PacketFetchMobArmour(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof MobArmourServerFilter) {
		
				MobArmourServerFilter filterCurrent = (MobArmourServerFilter)container.filterCurrent;
				IInventory inventory = filterCurrent.getInventory(container); 
					
				if(container.getTargetType() == TargetType.BLOCK) {
					TileEntity tile = player.world.getTileEntity(container.getBlockPos());
					if(tile instanceof TileEntityMobSpawner) {
						TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
						
						ItemStack[] mobArmor = SpawnerUtil.getMobArmor(spawner.getSpawnerBaseLogic(), this.minecartIndex);
						ItemStack[] mobHeld = SpawnerUtil.getMobHeldItems(spawner.getSpawnerBaseLogic(), this.minecartIndex);
						
						for(int i = 0; i < mobHeld.length; ++i) {
							inventory.setInventorySlotContents(1 - i, mobHeld[i]);
					    }
						for(int i = 0; i < mobArmor.length; ++i) {
							inventory.setInventorySlotContents(i + 2, mobArmor[i]);
					    }
					}
				}
				else if(container.getTargetType() == TargetType.ENTITY) {
					EntityLiving living = (EntityLiving)container.getEntity();
					inventory.setInventorySlotContents(5, living.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
					inventory.setInventorySlotContents(4, living.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
					inventory.setInventorySlotContents(3, living.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
					inventory.setInventorySlotContents(2, living.getItemStackFromSlot(EntityEquipmentSlot.FEET));
					inventory.setInventorySlotContents(1, living.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND));
					inventory.setInventorySlotContents(0, living.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND));
				}
					
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.update");
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
			}
		}
	}

}
