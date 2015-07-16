package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacket;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

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
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.slotIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.slotIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_ITEM_EDITOR, player.worldObj, this.slotIndex, 0, 0);
	}

}
