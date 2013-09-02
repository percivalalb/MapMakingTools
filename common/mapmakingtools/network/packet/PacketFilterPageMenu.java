package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketFilterPageMenu extends PacketMMT {

	public int selected;
	
	public PacketFilterPageMenu() {
		super(PacketTypeHandler.FILTER_PAGE_MENU, false);
	}
	
	public PacketFilterPageMenu(int selected) {
		this();
		this.selected = selected;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.selected = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(selected);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			if(playerMP.openContainer instanceof ContainerFilter) {
				ContainerFilter container = (ContainerFilter)playerMP.openContainer;
				container.setSelected(selected);
			}
		}
	}

}
