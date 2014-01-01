package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.inventory.ContainerWatchPlayer;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketChangeWatchPlayer extends PacketMMT {

	public String username;
	
	public PacketChangeWatchPlayer() {
		super(PacketTypeHandler.CHANGE_WATCH_PLAYER, false);
	}
	
	public PacketChangeWatchPlayer(String username) {
		this();
		this.username = username;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.username = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeUTF(username);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			if(player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				if(playerMP.openContainer != null && playerMP.openContainer instanceof ContainerWatchPlayer) {
					ContainerWatchPlayer container = (ContainerWatchPlayer)playerMP.openContainer;
					container.setWatchedPlayer(username);
				}
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}

}
