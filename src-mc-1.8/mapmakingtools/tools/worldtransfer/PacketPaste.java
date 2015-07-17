package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketPaste extends AbstractServerMessage {

	public String name;
	public List<BlockCache> list;
	public boolean firstSection, lastSection;
	
	public PacketPaste() {}
	public PacketPaste(String name, List<BlockCache> list, boolean firstSection, boolean lastSection) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.list = new ArrayList<BlockCache>();
		this.firstSection = packetbuffer.readBoolean();
		this.lastSection = packetbuffer.readBoolean();
		int size = packetbuffer.readInt();
		for(int i = 0; i < size; ++i)
			this.list.add(BlockCache.readFromPacketBuffer(packetbuffer));
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.name);
		packetbuffer.writeBoolean(this.firstSection);
		packetbuffer.writeBoolean(this.lastSection);
		packetbuffer.writeInt(this.list.size());
		for(int i = 0; i < this.list.size(); ++i)
			this.list.get(i).writeToPacketBuffer(packetbuffer);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		PlayerData data = WorldData.getPlayerData(player);
		
		ArrayList<BlockCache> newUndo = new ArrayList<BlockCache>();
		
		if(this.firstSection)
			data.lastPos = new BlockPos(player);
		
		for(BlockCache bse : this.list)
			newUndo.add(bse.restoreRelative(data.getPlayerWorld(), data.lastPos));
		
		if(this.firstSection)
			data.getActionStorage().addUndo(newUndo);
		else
			data.getActionStorage().getLastUndo().addAll(newUndo);
		
		if(this.lastSection) {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.worldtransfer.paste.complete", this.name);
			chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
			player.addChatMessage(chatComponent);
		}
	}

}
