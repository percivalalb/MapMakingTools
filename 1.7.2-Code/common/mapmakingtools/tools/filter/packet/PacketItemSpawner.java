package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import mapmakingtools.tools.filter.ItemSpawnerServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketItemSpawner extends IPacket {

	public int x, y, z;
	public int minecartIndex;
	
	public PacketItemSpawner() {}
	public PacketItemSpawner(int x, int y, int z, int minecartIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.minecartIndex = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		data.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof ItemSpawnerServerFilter) {
				
				ItemSpawnerServerFilter filterCurrent = (ItemSpawnerServerFilter)container.filterCurrent;
				if (tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					
					ItemStack item = container.getSlot(0).getStack().copy();
					SpawnerUtil.setItemType(spawner.func_145881_a(), item, this.minecartIndex);
					SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
					
			    	ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.changeitem.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
				}
			}
		}
	}

}
