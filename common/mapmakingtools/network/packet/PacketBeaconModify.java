package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketBeaconModify extends PacketMMT {

	public int x, y, z, primaryEffect, secondaryEffect, levels;
	
	public PacketBeaconModify() {
		super(PacketTypeHandler.BEACON_MODIFY, false);
	}
	
	public PacketBeaconModify(int x, int y, int z, int primaryEffect, int secondaryEffect, int levels) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.primaryEffect = primaryEffect;
		this.secondaryEffect = secondaryEffect;
		this.levels = levels;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.primaryEffect = data.readInt();
		this.secondaryEffect = data.readInt();
		this.levels = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeInt(primaryEffect);
		dos.writeInt(secondaryEffect);
		dos.writeInt(levels);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntityBeacon) {
				TileEntityBeacon beacon = (TileEntityBeacon)tile;
				beacon.setPrimaryEffect(this.primaryEffect);
				beacon.setSecondaryEffect(this.secondaryEffect);
				beacon.setLevels(this.levels);
				Packet packet = tile.getDescriptionPacket();
				if(packet != null) {
					if(player instanceof EntityPlayerMP) {
						EntityPlayerMP playermp = (EntityPlayerMP)player;
						playermp.playerNetServerHandler.sendPacketToPlayer(packet);
					}
				}
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

}
