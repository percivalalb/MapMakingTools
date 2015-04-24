package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
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
public class PacketCreeperProperties extends IPacket {

	public int x, y, z;
	public String fuseTime, explosionRadius;
	public int minecartIndex;
	
	public PacketCreeperProperties() {}
	public PacketCreeperProperties(int x, int y, int z, String fuseTime, String explosionRadius, int minecartIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.fuseTime = fuseTime;
		this.explosionRadius = explosionRadius;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.fuseTime = data.readUTF();
		this.explosionRadius = data.readUTF();
		this.minecartIndex = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		data.writeUTF(this.fuseTime);
		data.writeUTF(this.explosionRadius);
		data.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			

			if(!NumberParse.areIntegers(this.fuseTime, this.explosionRadius)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.creeperproperties.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
			}
			
			int fuseTimeNO = NumberParse.getInteger(this.fuseTime);
			int explosionRadiusNO = NumberParse.getInteger(this.explosionRadius);
			
			SpawnerUtil.setCreeperFuse(spawner.func_145881_a(), fuseTimeNO, this.minecartIndex);
			SpawnerUtil.setCreeperExplosionRadius(spawner.func_145881_a(), explosionRadiusNO, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.creeperproperties.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}
