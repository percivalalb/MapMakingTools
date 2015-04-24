package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacket;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.PlayerAccess;

/**
 * @author ProPercivalalb
 */
public class PacketOpenItemEditor extends IPacket {
	
	private int slotIndex;
	
	public PacketOpenItemEditor() {}
	public PacketOpenItemEditor(int slotIndex) {
		this.slotIndex = slotIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.slotIndex = data.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.slotIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_ITEM_EDITOR, player.worldObj, this.slotIndex, 0, 0);
	}

}
