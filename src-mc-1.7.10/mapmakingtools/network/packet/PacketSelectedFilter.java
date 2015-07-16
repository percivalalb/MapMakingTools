package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketSelectedFilter extends IPacket {

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
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			container.setSelected(this.selected);
		}
	}

}
