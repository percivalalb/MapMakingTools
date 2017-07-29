package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;

import mapmakingtools.lib.PacketLib;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
		this.name = packetbuffer.readString(Integer.MAX_VALUE / 4);
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
					PacketDispatcher.sendToServer(new PacketPaste(this.name, part, first, index == area.size() - 1, WorldTransferList.mapPos.get(this.name).get(0), WorldTransferList.mapPos.get(this.name).get(1)));
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
	
	}

}
