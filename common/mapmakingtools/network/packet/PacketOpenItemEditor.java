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
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketOpenItemEditor extends PacketMMT {
	
	private int slotNo;
	
	public PacketOpenItemEditor() {
		super(PacketTypeHandler.OPEN_ITEM_EDITOR, false);
	}
	
	public PacketOpenItemEditor(int slotNo) {
		this();
		this.slotNo = slotNo;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.slotNo = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(slotNo);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_ITEM_EDITOR, player.worldObj, slotNo, 0, 0);
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}

}
