package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketSkullModify extends PacketMMT {

	public String name;
	
	public PacketSkullModify() {
		super(PacketTypeHandler.SKULL_MODIFY, false);
	}
	
	public PacketSkullModify(String newName) {
		this();
		this.name = newName;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.name = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeUTF(name);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			if(ItemStackHelper.isItem(player.getHeldItem(), Item.skull)) {
				if(player.getHeldItem().getItemDamage() == 3) {
					ItemStackHelper.setString(player.getHeldItem(), NBTData.SKULL_NAME, this.name);
				}
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

}
