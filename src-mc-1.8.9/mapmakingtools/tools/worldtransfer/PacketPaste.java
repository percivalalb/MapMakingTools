package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
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
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketPaste extends AbstractServerMessage {

	public String name;
	public List<BlockCache> list;
	public boolean firstSection, lastSection;
	
	public BlockPos playerPos, firstPos, secondPos;	
	public static final Hashtable<String, List<BlockPos>> mapPos = new Hashtable<String, List<BlockPos>>();
	
	
	public PacketPaste() {}
	public PacketPaste(String name, List<BlockCache> list, boolean firstSection, boolean lastSection, BlockPos firstPos, BlockPos secondPos) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
		this.firstPos = firstPos;
		this.secondPos = secondPos;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.list = new ArrayList<BlockCache>();
		this.firstSection = packetbuffer.readBoolean();
		this.lastSection = packetbuffer.readBoolean();
		if(this.firstSection) {
			this.playerPos = packetbuffer.readBlockPos();
			this.firstPos = packetbuffer.readBlockPos();
			this.secondPos = packetbuffer.readBlockPos();
		}
		
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
			packetbuffer.writeBlockPos(this.firstPos);
			packetbuffer.writeBlockPos(this.secondPos);
		}
		
		packetbuffer.writeInt(this.list.size());
		
		for(int i = 0; i < this.list.size(); ++i)
			this.list.get(i).writeToPacketBufferCompact(packetbuffer);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		PlayerData data = WorldData.getPlayerData(player);
		
		ArrayList<BlockCache> newUndo = new ArrayList<BlockCache>();
		
		if(this.firstSection)
			this.mapPos.put(this.name, Arrays.asList(this.firstPos, this.secondPos, this.playerPos, new BlockPos(player)));
		
		
		
		int index = this.firstSection ? 0 : data.getActionStorage().getLastUndo().size();
		
		
		int xDiff = Math.abs(this.mapPos.get(this.name).get(0).getX() - this.mapPos.get(this.name).get(1).getX()) + 1;
		int yDiff = Math.abs(this.mapPos.get(this.name).get(0).getY() - this.mapPos.get(this.name).get(1).getY()) + 1;
		int zDiff = Math.abs(this.mapPos.get(this.name).get(0).getZ() - this.mapPos.get(this.name).get(1).getZ()) + 1;
		
		int x = Math.min(this.mapPos.get(this.name).get(0).getX(), this.mapPos.get(this.name).get(1).getX());
		int y = Math.min(this.mapPos.get(this.name).get(0).getY(), this.mapPos.get(this.name).get(1).getY());
		int z = Math.min(this.mapPos.get(this.name).get(0).getZ(), this.mapPos.get(this.name).get(1).getZ());
		
		BlockPos lowestPos = new BlockPos(x, y, z);
		
		for(BlockCache bse : this.list) {
			bse.playerPos = this.firstSection ? this.playerPos : this.mapPos.get(this.name).get(2);

			bse.pos = lowestPos.add(index % xDiff, MathHelper.floor_double((index % (yDiff * xDiff)) / xDiff), MathHelper.floor_double(index / (yDiff * xDiff)));
			
			index += 1;
		}
		
		for(BlockCache bse : this.list)
			newUndo.add(bse.restoreRelative(data.getPlayerWorld(), this.mapPos.get(this.name).get(3)));
		
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
