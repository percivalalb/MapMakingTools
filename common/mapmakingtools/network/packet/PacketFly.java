package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketFly extends PacketMMT {

	public int speed;
	
	public PacketFly() {
		super(PacketTypeHandler.FLY, false);
	}
	
	public PacketFly(int speed) {
		this();
		this.speed = speed;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.speed = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(speed);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		FlyHandler.setFlySpeed(player, speed);
	}

}
