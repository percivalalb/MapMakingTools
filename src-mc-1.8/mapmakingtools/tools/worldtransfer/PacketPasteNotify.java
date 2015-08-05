package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;

import mapmakingtools.lib.PacketLib;
import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketPasteNotify extends AbstractClientMessage {

	public String name;
	
	public PacketPasteNotify() {}
	public PacketPasteNotify(String name) {
		this.name = name;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.name);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(!WorldTransferList.hasName(this.name))
			return;
		
		ArrayList<BlockCache> area = WorldTransferList.getAreaFromName(this.name);
		
		try {
			
			boolean send = false;
			int packetCount = 0;
			
			int index = 0;
			boolean first = true;
			
			int start = 0;
			ArrayList<BlockCache> part = new ArrayList<BlockCache>();
			
			for(BlockCache cache : area) {
				int size = cache.calculateSizeCompact();
				
				if(start + size <= PacketLib.MAX_SIZE_TO_SERVER) {
					part.add(cache);
					
					start += size;
					send = index == area.size() - 1;
				}
				else
					send = true;

				
				if(send) {
					PacketDispatcher.sendToServer(new PacketPaste(this.name, part, first, index == area.size() - 1));
					packetCount += 1;
					start = 0;
					first = false;
					part.clear();
				}
				
				index += 1;
			}
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("packets: " + packetCount, packetCount);
			chatComponent.getChatStyle().setColor(EnumChatFormatting.AQUA);
			player.addChatMessage(chatComponent);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
	}

}
