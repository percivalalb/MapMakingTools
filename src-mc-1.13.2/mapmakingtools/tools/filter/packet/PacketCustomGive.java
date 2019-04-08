package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.CommandBlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author ProPercivalalb
 */
public class PacketCustomGive {
	
	public static void encode(PacketCustomGive msg, PacketBuffer buf) {
		
	}
	
	public static PacketCustomGive decode(PacketBuffer buf) {
		return new PacketCustomGive();
	}
	
	public static class Handler {
        public static void handle(final PacketCustomGive msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		

        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			CommandBlockBaseLogic logic = CommandBlockUtil.getCommandLogic(container);

        			if(container.getSlot(0).getStack() != null) {
            			String command = "/give @p";
            			ItemStack stack = container.getSlot(0).getStack().copy();
            			command += " " + ForgeRegistries.ITEMS.getKey(stack.getItem());
            			command += " " + stack.getCount();
            			
            			if(stack.hasTag())
            				command += " " + String.valueOf(stack.getTag());
        				
            			CommandBlockUtil.setCommand(logic, command);
            			
            			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.customgive.complete", command);
        				chatComponent.getStyle().setItalic(true);
        				player.sendMessage(chatComponent);
            		}
        			
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}

}
