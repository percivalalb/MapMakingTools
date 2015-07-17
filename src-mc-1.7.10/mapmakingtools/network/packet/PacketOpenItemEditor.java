package mapmakingtools.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.MapMakingTools;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketOpenItemEditor extends AbstractServerMessage {
	
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
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		
		player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_ITEM_EDITOR, player.worldObj, this.slotIndex, 0, 0);
		return null;
	}

}
