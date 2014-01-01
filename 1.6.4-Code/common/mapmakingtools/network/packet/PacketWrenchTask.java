package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.MapMakingTools;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.core.util.WrenchTasks;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketWrenchTask extends PacketMMT {
	
	public enum Mode {
		ENTITY,
		BLOCK;
	}
	
	public int x, y, z;
	public int entityId;
	public Mode mode = Mode.BLOCK;
	
	public PacketWrenchTask() {
		super(PacketTypeHandler.WRENCH_TASK, false);
	}
	
	public PacketWrenchTask(int x, int y, int z) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.mode = Mode.BLOCK;
	}
	
	public PacketWrenchTask(int entityId) {
		this();
		this.entityId = entityId;
		this.mode = Mode.ENTITY;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.mode = data.readBoolean() ? Mode.BLOCK : Mode.ENTITY;
		if(mode == Mode.ENTITY) {
			this.entityId = data.readInt();
		}
		else {
			this.x = data.readInt();
			this.y = data.readInt();
			this.z = data.readInt();
		}
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeBoolean(mode == Mode.BLOCK);
		if(mode == Mode.ENTITY) {
			dos.writeInt(entityId);
		}
		else {
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
		}
	}
	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		LogHelper.logDebug("Execute");
		if(mode == Mode.ENTITY) {
			LogHelper.logDebug("Remove");
			WrenchTasks.removeTaskEntity(player, player.worldObj, entityId);
		}
		else {
			WrenchTasks.removeTaskBlock(player, player.worldObj, x, y, z);	
		}
	}

}
