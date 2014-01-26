package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PacketSelectedFilter extends MMTPacket {

	public int selected;
	
	public PacketSelectedFilter() {}
	public PacketSelectedFilter(int selected) {
		this.selected = selected;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		this.selected = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(selected);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			container.setSelected(this.selected);
		}
	}

}
