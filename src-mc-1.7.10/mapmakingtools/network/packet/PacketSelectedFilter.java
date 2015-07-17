package mapmakingtools.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketSelectedFilter extends AbstractServerMessage {

	public int selected;
	
	public PacketSelectedFilter() {}
	public PacketSelectedFilter(int selected) {
		this.selected = selected;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.selected = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(selected);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			container.setSelected(this.selected);
		}
		return null;
	}

}
