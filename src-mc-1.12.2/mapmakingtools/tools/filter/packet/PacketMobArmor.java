package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmor extends AbstractServerMessage {

	public int minecartIndex;
	
	public PacketMobArmor() {}
	public PacketMobArmor(int minecartIndex) {
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
			if(container.filterCurrent instanceof MobArmorServerFilter) {
		
				MobArmorServerFilter filterCurrent = (MobArmorServerFilter)container.filterCurrent;
				IInventory inventory = filterCurrent.getInventory(container); 
					
				if(container.getTargetType() == TargetType.BLOCK) {
					TileEntity tile = player.world.getTileEntity(container.getBlockPos());
					if(tile instanceof TileEntityMobSpawner) {
						TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
						SpawnerUtil.setMobArmor(spawner.getSpawnerBaseLogic(), inventory.getStackInSlot(5), inventory.getStackInSlot(4), inventory.getStackInSlot(3), inventory.getStackInSlot(2), inventory.getStackInSlot(1), inventory.getStackInSlot(0), this.minecartIndex);
						PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
						PacketUtil.sendTileEntityUpdateToWatching(spawner);
					}
				}
				else if(container.getTargetType() == TargetType.ENTITY) {
					EntityLiving living = (EntityLiving)container.getEntity();
					living.setItemStackToSlot(EntityEquipmentSlot.HEAD, inventory.getStackInSlot(5));
					living.setItemStackToSlot(EntityEquipmentSlot.CHEST, inventory.getStackInSlot(4));
					living.setItemStackToSlot(EntityEquipmentSlot.LEGS, inventory.getStackInSlot(3));
					living.setItemStackToSlot(EntityEquipmentSlot.FEET, inventory.getStackInSlot(2));
					living.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inventory.getStackInSlot(1));
					living.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, inventory.getStackInSlot(0));
				}
					
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.complete");
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
			}	
		}
	}

}
