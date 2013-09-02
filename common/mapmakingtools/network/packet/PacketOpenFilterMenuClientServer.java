package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class PacketOpenFilterMenuClientServer extends PacketMMT {
	
	public enum Mode {
		ENTITY,
		BLOCK;
	}
	
	public int x, y, z;
	public int entityId;
	public Mode mode = Mode.BLOCK;
	
	public PacketOpenFilterMenuClientServer() {
		super(PacketTypeHandler.FITLER_MENU, false);
	}
	
	public PacketOpenFilterMenuClientServer(int x, int y, int z) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.mode = Mode.BLOCK;
	}
	
	public PacketOpenFilterMenuClientServer(int entityId) {
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
		if(GeneralHelper.inCreative(player)) {
			if(mode == Mode.ENTITY) {
				if(!WrenchTasks.isThereTaskEntity(player.worldObj, entityId)) {
					PacketTypeHandler.populatePacketAndSendToClient(new PacketOpenFilterMenuServerClient(player.worldObj, entityId), (EntityPlayerMP)player);
					player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_FILTERS_2, player.worldObj, entityId, 0, 0);
				}
				else {
					EntityPlayer taskPlayer = WrenchTasks.getPlayerTaskEntity(player.worldObj, entityId);
					player.sendChatToPlayer(ChatMessageComponent.func_111082_b("error.wrenchTask.entity", new Object[] {taskPlayer.getTranslatedEntityName()}));
				}
			}
			else {
				if(!WrenchTasks.isThereTaskBlock(player.worldObj, x, y, z)) {
					PacketTypeHandler.populatePacketAndSendToClient(new PacketOpenFilterMenuServerClient(player.worldObj, x, y, z), (EntityPlayerMP)player);
					player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_FILTERS_1, player.worldObj, x, y, z);	
				}
				else {
					EntityPlayer taskPlayer = WrenchTasks.getPlayerTaskBlock(player.worldObj,  x, y, z);
					player.sendChatToPlayer(ChatMessageComponent.func_111082_b("error.wrenchTask.block", new Object[] {taskPlayer.getTranslatedEntityName()}));
				}
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

}
