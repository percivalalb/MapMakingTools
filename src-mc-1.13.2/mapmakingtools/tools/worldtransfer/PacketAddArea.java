package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketAddArea extends AbstractClientMessage {

	public String name;
	public ArrayList<BlockCache> list;
	public boolean firstSection, lastSection;
	public PlayerData data;
	
	public BlockPos playerPos, firstPos, secondPos;
	
	public PacketAddArea() {}
	public PacketAddArea(String name, ArrayList<BlockCache> list, boolean firstSection, boolean lastSection, PlayerData data) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
		this.data = data;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.firstSection = packetbuffer.readBoolean();
		this.lastSection = packetbuffer.readBoolean();
		if(this.firstSection) {
			this.playerPos = packetbuffer.readBlockPos();
			this.firstPos = packetbuffer.readBlockPos();
			this.secondPos = packetbuffer.readBlockPos();
		}
		
		this.list = new ArrayList<BlockCache>();
		int size = packetbuffer.readInt();
		for(int i = 0; i < size; ++i)
			this.list.add(BlockCache.readFromPacketBufferCompact(packetbuffer));

	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.name);
		packetbuffer.writeBoolean(this.firstSection);
		packetbuffer.writeBoolean(this.lastSection);
		if(this.firstSection) {
			packetbuffer.writeBlockPos(this.list.get(0).playerPos);
			packetbuffer.writeBlockPos(this.data.getFirstPoint());
			packetbuffer.writeBlockPos(this.data.getSecondPoint());
		}
		
		packetbuffer.writeInt(this.list.size());
		for(int i = 0; i < this.list.size(); ++i)
			this.list.get(i).writeToPacketBufferCompact(packetbuffer);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(this.firstSection)
			WorldTransferList.mapPos.put(this.name, Arrays.asList(this.firstPos, this.secondPos));
		
		int index = this.firstSection ? 0 : WorldTransferList.getAreaFromName(this.name).size();
		
		int xDiff = Math.abs(WorldTransferList.mapPos.get(this.name).get(0).getX() - WorldTransferList.mapPos.get(this.name).get(1).getX()) + 1;
		int yDiff = Math.abs(WorldTransferList.mapPos.get(this.name).get(0).getY() - WorldTransferList.mapPos.get(this.name).get(1).getY()) + 1;
		int zDiff = Math.abs(WorldTransferList.mapPos.get(this.name).get(0).getZ() - WorldTransferList.mapPos.get(this.name).get(1).getZ()) + 1;
		
		int x = Math.min(WorldTransferList.mapPos.get(this.name).get(0).getX(), WorldTransferList.mapPos.get(this.name).get(1).getX());
		int y = Math.min(WorldTransferList.mapPos.get(this.name).get(0).getY(), WorldTransferList.mapPos.get(this.name).get(1).getY());
		int z = Math.min(WorldTransferList.mapPos.get(this.name).get(0).getZ(), WorldTransferList.mapPos.get(this.name).get(1).getZ());
		
		BlockPos lowestPos = new BlockPos(x, y, z);
		
		for(BlockCache bse : this.list) {
			bse.playerPos = this.firstSection ? this.playerPos : WorldTransferList.getAreaFromName(this.name).get(0).playerPos;

			bse.pos = lowestPos.add(index % xDiff, MathHelper.floor((index % (yDiff * xDiff)) / xDiff), MathHelper.floor(index / (yDiff * xDiff)));
			
			index += 1;
		}
		
		WorldTransferList.put(this.name, this.firstSection, this.lastSection, this.list);

		if(this.lastSection) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete", name);
			chatComponent.getStyle().setColor(TextFormatting.GREEN);
			player.sendMessage(chatComponent);
			chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete2", this.name);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
			
			WorldTransferList.saveToFile();
		}
	}

}
