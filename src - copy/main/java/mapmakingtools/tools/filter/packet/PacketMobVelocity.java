package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class PacketMobVelocity extends IPacket {

	public int x, y, z;
	public String xMotion, yMotion, zMotion;
	public int minecartIndex;
	
	public PacketMobVelocity() {}
	public PacketMobVelocity(int x, int y, int z, String xMotion, String yMotion, String zMotion, int minecartIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.xMotion = data.readUTF();
		this.yMotion = data.readUTF();
		this.zMotion = data.readUTF();
		this.minecartIndex = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		data.writeUTF(this.xMotion);
		data.writeUTF(this.yMotion);
		data.writeUTF(this.zMotion);
		data.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areDoubles(this.xMotion, this.yMotion, this.zMotion)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobvelocity.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
			}
			
			double xMotionNO = NumberParse.getDouble(this.xMotion);
			double yMotionNO = NumberParse.getDouble(this.yMotion);
			double zMotionNO = NumberParse.getDouble(this.zMotion);
			
			SpawnerUtil.setVelocity(spawner.func_145881_a(), xMotionNO, yMotionNO, zMotionNO, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobvelocity.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}
}
