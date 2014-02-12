package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.IPacket;
import net.minecraft.entity.player.EntityPlayer;

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
	public void read(DataInputStream data) throws IOException {
		this.selected = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(selected);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			container.setSelected(this.selected);
		}
	}

}
