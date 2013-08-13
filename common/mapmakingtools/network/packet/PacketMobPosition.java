package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.FilterHelper;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketMobPosition extends PacketMMT {

	public int x, y, z;
	public String xMotion, yMotion, zMotion;
	public boolean relative;
	
	public PacketMobPosition() {
		super(PacketTypeHandler.MOB_POSITION, false);
	}
	
	public PacketMobPosition(int x, int y, int z, String xMotion, String yMotion, String zMotion, boolean relative) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.relative = relative;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.xMotion = data.readUTF();
		this.yMotion = data.readUTF();
		this.zMotion = data.readUTF();
		this.relative = data.readBoolean();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeUTF(xMotion);
		dos.writeUTF(yMotion);
		dos.writeUTF(zMotion);
		dos.writeBoolean(relative);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				double xMotionNO;
				double yMotionNO;
				double zMotionNO;
				if(!FilterHelper.isDouble(xMotion) || !FilterHelper.isDouble(yMotion) || !FilterHelper.isDouble(zMotion)) {
					player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.creeperExplosion.notInt"));
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
			
				player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.mo.complete"));
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}
}
