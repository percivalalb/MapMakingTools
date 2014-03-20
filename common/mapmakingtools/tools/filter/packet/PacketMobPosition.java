package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class PacketMobPosition extends IPacket {

	public int x, y, z;
	public String xMotion, yMotion, zMotion;
	public boolean relative;
	
	public PacketMobPosition() {}
	public PacketMobPosition(int x, int y, int z, String xMotion, String yMotion, String zMotion, boolean relative) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.relative = relative;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.xMotion = data.readUTF();
		this.yMotion = data.readUTF();
		this.zMotion = data.readUTF();
		this.relative = data.readBoolean();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		data.writeUTF(this.xMotion);
		data.writeUTF(this.yMotion);
		data.writeUTF(this.zMotion);
		data.writeBoolean(this.relative);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			double xMotionNO;
			double yMotionNO;
			double zMotionNO;
			if(!FilterHelper.isDouble(xMotion) || !FilterHelper.isDouble(yMotion) || !FilterHelper.isDouble(zMotion)) {
				player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("filter.creeperExplosion.notInt"));
				return;
			}
			xMotionNO = FilterHelper.getDouble(xMotion);
			yMotionNO = FilterHelper.getDouble(yMotion);
			zMotionNO = FilterHelper.getDouble(zMotion);
			if(relative) {
				xMotionNO += x;
				yMotionNO += y;
				zMotionNO += z;
			}
				
			SpawnerHelper.setPosition(tile, xMotionNO, yMotionNO, zMotionNO);
			
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("filter.mo.complete"));
		}
	}
}
