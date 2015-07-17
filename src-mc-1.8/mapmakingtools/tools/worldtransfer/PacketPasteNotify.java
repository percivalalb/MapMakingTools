package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;

import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
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
		
		ArrayList<Integer> sendData = WorldTransferList.getSendDataFromName(this.name);
		ArrayList<BlockCache> area = WorldTransferList.getAreaFromName(this.name);
		int start = 0;
		
		for(int i = 0; i < sendData.size(); ++i) {
			int amount = sendData.get(i);
			ArrayList<BlockCache> part = new ArrayList<BlockCache>();
			
			for(int j = start; j < start + amount; ++j)
				part.add(area.get(j));
			
			PacketDispatcher.sendToServer(new PacketPaste(this.name, part, i == 0, i == sendData.size() - 1));
			start += amount;
		}
	}

}
