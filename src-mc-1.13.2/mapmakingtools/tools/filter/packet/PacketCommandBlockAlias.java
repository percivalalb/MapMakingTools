package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.CommandBlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketCommandBlockAlias {

	public String name;
	
	public PacketCommandBlockAlias(String name) {
		this.name = name;
	}

	public static void encode(PacketCommandBlockAlias msg, PacketBuffer buf) {
		buf.writeString(msg.name);
	}
	
	public static PacketCommandBlockAlias decode(PacketBuffer buf) {
		String name = buf.readString(32767);
		return new PacketCommandBlockAlias(name);
	}
	
	public static class Handler {
        public static void handle(final PacketCommandBlockAlias msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;

        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			CommandBlockBaseLogic logic = CommandBlockUtil.getCommandLogic(container);

        			CommandBlockUtil.setName(logic, msg.name);
        			
            		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.commandblockalias.complete", msg.name);
        			chatComponent.getStyle().setItalic(true);
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.commandblockalias.complete", msg.name).applyTextStyle(TextFormatting.ITALIC));
        			
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
