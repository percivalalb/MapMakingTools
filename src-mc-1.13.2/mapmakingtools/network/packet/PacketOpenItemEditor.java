package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketOpenItemEditor {
	
	private int slotIndex;
	
	public PacketOpenItemEditor(int slotIndex) {
		this.slotIndex = slotIndex;
	}

	public static void encode(PacketOpenItemEditor msg, PacketBuffer buf) {
		buf.writeInt(msg.slotIndex);
	}
	
	public static PacketOpenItemEditor decode(PacketBuffer buf) {
		int slotIndex = buf.readInt();
		return new PacketOpenItemEditor(slotIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketOpenItemEditor msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		//TODO player.openGui(MapMakingTools.INSTANCE, CommonProxy.GUI_ID_ITEM_EDITOR, player.world, this.slotIndex, 0, 0);
            });
            
            ctx.get().setPacketHandled(true);
        }
	}
}
