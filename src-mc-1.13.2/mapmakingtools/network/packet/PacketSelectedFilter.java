package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.inventory.ContainerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketSelectedFilter {

	public int selected;
	
	public PacketSelectedFilter(int selected) {
		this.selected = selected;
	}

	public static void encode(PacketSelectedFilter msg, PacketBuffer buf) {
		buf.writeInt(msg.selected);
	}
	
	public static PacketSelectedFilter decode(PacketBuffer buf) {
		int selected = buf.readInt();
		return new PacketSelectedFilter(selected);
	}
	
	public static class Handler {
        public static void handle(final PacketSelectedFilter msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	
            	if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			container.setSelected(msg.selected);
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
