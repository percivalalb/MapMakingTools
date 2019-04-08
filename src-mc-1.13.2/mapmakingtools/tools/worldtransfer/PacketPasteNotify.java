package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import mapmakingtools.client.gui.button.GuiHorizontalTab.Side;
import mapmakingtools.lib.PacketLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class PacketPasteNotify {

	public String name;
	
	public PacketPasteNotify(String name) {
		this.name = name;
	}
	
	public static void encode(PacketPasteNotify msg, PacketBuffer buf) {
		buf.writeString(msg.name);
	}
	
	public static PacketPasteNotify decode(PacketBuffer buf) {
		String name = buf.readString(Integer.MAX_VALUE / 4);
		return new PacketPasteNotify(name);
	}
	
	public static class Handler {
        public static void handle(final PacketPasteNotify msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(!WorldTransferList.hasName(msg.name))
        			return;
        		
        		ArrayList<BlockCache> area = WorldTransferList.getAreaFromName(msg.name);
        		
        		try {
        			
        			int packetCount = 0;
        			
        			int index = 0;
        			boolean first = true;
        			
        			int start = 0;
        			ArrayList<BlockCache> part = new ArrayList<BlockCache>();
        			
        			for(BlockCache cache : area) {
        				boolean notFinal = false;
        				
        				int size = cache.calculateSizeCompact();
        				if(start + size <= PacketLib.MAX_SIZE_TO_SERVER) {
        					part.add(cache);
        					
        					start += size;
        				}
        				else
        					notFinal = true;

        				
        				if(notFinal || index == area.size() - 1) {
        					PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketPaste(msg.name, part, first, index == area.size() - 1, WorldTransferList.mapPos.get(msg.name).get(0), WorldTransferList.mapPos.get(msg.name).get(1)));
        					packetCount += 1;
        					start = 0;
        					first = false;
        					part.clear();
        					
        					if(notFinal) {
        						part.add(cache);
        						start += size;
        					}
        				}
        				
        				index += 1;
        			}
        			
        			TextComponentTranslation chatComponent = new TextComponentTranslation("packets: " + packetCount, packetCount);
        			chatComponent.getStyle().setColor(TextFormatting.AQUA);
        			player.sendMessage(chatComponent);
        			
        		}
        		catch(Exception e) {
        			e.printStackTrace();
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
