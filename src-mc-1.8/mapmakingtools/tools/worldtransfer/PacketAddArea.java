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
	public ArrayList<Integer> sendData;
	
	public PacketAddArea() {}
	public PacketAddArea(String name, ArrayList<BlockCache> list, ArrayList<Integer> sendData, boolean firstSection, boolean lastSection) {
		this.name = name;
		this.list = list;
		this.sendData = sendData;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.firstSection = packetbuffer.readBoolean();
		this.lastSection = packetbuffer.readBoolean();
		this.list = new ArrayList<BlockCache>();
		this.sendData = new ArrayList<Integer>();
		int size = packetbuffer.readInt();
		for(int i = 0; i < size; ++i)
			this.list.add(BlockCache.readFromPacketBuffer(packetbuffer));
		
		if(this.firstSection) {
			int s = packetbuffer.readInt();
			for(int i = 0; i < s; ++i)
				this.sendData.add(packetbuffer.readInt());
		}
			
			
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.name);
		packetbuffer.writeBoolean(this.firstSection);
		packetbuffer.writeBoolean(this.lastSection);
		packetbuffer.writeInt(this.list.size());
		for(int i = 0; i < this.list.size(); ++i)
			this.list.get(i).writeToPacketBuffer(packetbuffer);
		
		if(this.firstSection) {
			packetbuffer.writeInt(this.sendData.size());
			for(int i = 0; i < this.sendData.size(); ++i)
				packetbuffer.writeInt(this.sendData.get(i));
		}
			
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		WorldTransferList.put(this.name, this.firstSection, this.lastSection, this.list, this.sendData);

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
