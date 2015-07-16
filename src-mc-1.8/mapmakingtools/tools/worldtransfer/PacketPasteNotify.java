package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketPasteNotify extends IPacket {

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
	public void execute(EntityPlayer player) {
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
			
			MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketPaste(this.name, part, i == 0, i == sendData.size() - 1));
			start += amount;
		}
	}

}
