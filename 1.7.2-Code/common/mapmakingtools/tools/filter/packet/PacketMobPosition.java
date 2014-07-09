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

public class PacketMobPosition extends IPacket {

	public int x, y, z;
	public String xPos, yPos, zPos;
	public boolean relative;
	public int minecartIndex;
	
	public PacketMobPosition() {}
	public PacketMobPosition(int x, int y, int z, String xPos, String yPos, String zPos, boolean relative, int minecartIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.relative = relative;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.xPos = data.readUTF();
		this.yPos = data.readUTF();
		this.zPos = data.readUTF();
		this.relative = data.readBoolean();
		this.minecartIndex = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		data.writeUTF(this.xPos);
		data.writeUTF(this.yPos);
		data.writeUTF(this.zPos);
		data.writeBoolean(this.relative);
		data.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areDoubles(this.xPos, this.yPos, this.zPos)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobposition.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
			}
			
			double xPosNO = NumberParse.getDouble(this.xPos);
			double yPosNO = NumberParse.getDouble(this.yPos);
			double zPosNO = NumberParse.getDouble(this.zPos);
			if(this.relative) {
				xPosNO += x;
				yPosNO += y;
				zPosNO += z;
			}
			
			SpawnerUtil.setPosition(spawner.func_145881_a(), xPosNO, yPosNO, zPosNO, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobposition.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}
}
