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

public class PacketMobVelocity extends PacketMMT {

	public int x, y, z;
	public String xMotion, yMotion, zMotion;
	
	public PacketMobVelocity() {
		super(PacketTypeHandler.MOB_VELOCITY, false);
	}
	
	public PacketMobVelocity(int x, int y, int z, String xMotion, String yMotion, String zMotion) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.xMotion = data.readUTF();
		this.yMotion = data.readUTF();
		this.zMotion = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeUTF(xMotion);
		dos.writeUTF(yMotion);
		dos.writeUTF(zMotion);
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
					player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("filter.creeperExplosion.notInt"));
					return;
				}
				xMotionNO = FilterHelper.getDouble(xMotion);
				yMotionNO = FilterHelper.getDouble(yMotion);
				zMotionNO = FilterHelper.getDouble(zMotion);
				SpawnerHelper.setMotion(tile, xMotionNO, yMotionNO, zMotionNO);
			
				player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.mobVelocity.complete", new Object[] {xMotion, yMotion, zMotion}));
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}
}
