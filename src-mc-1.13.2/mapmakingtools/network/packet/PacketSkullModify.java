package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.item.nbt.SkullNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketSkullModify {

	public String name;
	
	public PacketSkullModify(String name) {
		this.name = name;
	}
	
	public static void encode(PacketSkullModify msg, PacketBuffer buf) {
		buf.writeString(msg.name, 32);
	}
	
	public static PacketSkullModify decode(PacketBuffer buf) {
		String name = buf.readString(32);
		return new PacketSkullModify(name);
	}
	
	public static class Handler {
        public static void handle(final PacketSkullModify msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		ItemStack item = player.getHeldItemMainhand();
        		if(item == null)
        			return;
        		
        		if(item != null && Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(Items.SKELETON_SKULL)) { // TODO && item.getItemDamage() == 3) {
        			SkullNBT.setSkullName(item, msg.name);
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
