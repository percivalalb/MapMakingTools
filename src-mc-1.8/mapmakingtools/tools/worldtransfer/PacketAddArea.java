package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;

import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
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
public class PacketAddArea extends AbstractClientMessage {

	public String name;
	public ArrayList<BlockCache> list;
	public boolean firstSection, lastSection;
	
	public PacketAddArea() {}
	public PacketAddArea(String name, ArrayList<BlockCache> list, boolean firstSection, boolean lastSection) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.firstSection = packetbuffer.readBoolean();
		this.lastSection = packetbuffer.readBoolean();
		this.list = new ArrayList<BlockCache>();
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
		
		WorldTransferList.put(this.name, this.firstSection, this.lastSection, this.list);

		if(this.lastSection) {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete", name);
			chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
			player.addChatMessage(chatComponent);
			chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete2", this.name);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
			WorldTransferList.saveToFile();
		}
	}

}
