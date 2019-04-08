package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketItemEditorUpdate {
	
	public int slotIndex;
	public ItemStack stack;
	
	public PacketItemEditorUpdate(ItemStack stack, int slotIndex) {
		this.stack = stack;
		this.slotIndex = slotIndex;
	}
	
	public static void encode(PacketItemEditorUpdate msg, PacketBuffer buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeItemStack(msg.stack);
	}
	
	public static PacketItemEditorUpdate decode(PacketBuffer buf) {
		int slotIndex = buf.readInt();
		ItemStack stack = buf.readItemStack();
		return new PacketItemEditorUpdate(stack, slotIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketItemEditorUpdate msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		player.inventory.setInventorySlotContents(msg.slotIndex, msg.stack);
        		player.inventory.markDirty();
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
