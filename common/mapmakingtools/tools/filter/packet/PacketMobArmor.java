package mapmakingtools.tools.filter.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.network.packet.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmor extends IPacket {

	public int x, y, z;
	public int minecartIndex;
	
	public PacketMobArmor() {}
	public PacketMobArmor(int x, int y, int z, int minecartIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		this.x = bytes.readInt();
		this.y = bytes.readInt();
		this.z = bytes.readInt();
		this.minecartIndex = bytes.readInt();
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		bytes.writeInt(x);
		bytes.writeInt(y);
		bytes.writeInt(z);
		bytes.writeInt(minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.func_147438_o(x, y, z);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof MobArmorServerFilter) {
				if(tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					MobArmorServerFilter filterCurrent = (MobArmorServerFilter)container.filterCurrent;
					ItemStack[] stack = filterCurrent.getInventory(container).contents; 
					SpawnerUtil.setMobArmor(spawner.func_145881_a(), stack[4], stack[3], stack[2], stack[1], stack[0], this.minecartIndex);
					SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
					
				    ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobArmor.complete");
					chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
					player.func_145747_a(chatComponent);
				}
			}
		}
	}

}
