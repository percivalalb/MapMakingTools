package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Supplier;

import mapmakingtools.client.gui.button.GuiHorizontalTab.Side;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketPaste {

	public String name;
	public List<BlockCache> list;
	public boolean firstSection, lastSection;
	
	public BlockPos playerPos, firstPos, secondPos;	
	public static final Hashtable<String, List<BlockPos>> mapPos = new Hashtable<String, List<BlockPos>>();
	
	public PacketPaste(String name, List<BlockCache> list, boolean firstSection, boolean lastSection, BlockPos firstPos, BlockPos secondPos) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
		this.firstPos = firstPos;
		this.secondPos = secondPos;
	}
	
	public PacketPaste(String name, List<BlockCache> list, boolean firstSection, boolean lastSection, BlockPos playerPos, BlockPos firstPos, BlockPos secondPos) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
		this.playerPos = playerPos;
		this.firstPos = firstPos;
		this.secondPos = secondPos;
	}
	
	public static void encode(PacketPaste msg, PacketBuffer buf) {
		buf.writeString(msg.name);
		buf.writeBoolean(msg.firstSection);
		buf.writeBoolean(msg.lastSection);
		if(msg.firstSection) {
			buf.writeBlockPos(msg.list.get(0).playerPos);
			buf.writeBlockPos(msg.firstPos);
			buf.writeBlockPos(msg.secondPos);
		}
		
		buf.writeInt(msg.list.size());
		
		for(int i = 0; i < msg.list.size(); ++i)
			msg.list.get(i).writeToPacketBufferCompact(buf);
	}
	
	public static PacketPaste decode(PacketBuffer buf) {
		String name = buf.readString(Integer.MAX_VALUE / 4);
		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
		boolean firstSection = buf.readBoolean();
		boolean lastSection = buf.readBoolean();
		BlockPos playerPos = null;
		BlockPos firstPos = null;
		BlockPos secondPos = null;
		if(firstSection) {
			playerPos = buf.readBlockPos();
			firstPos = buf.readBlockPos();
			secondPos = buf.readBlockPos();
		}
		
		int size = buf.readInt();
		for(int i = 0; i < size; ++i)
			list.add(BlockCache.readFromPacketBufferCompact(buf));
		return new PacketPaste(name, list, firstSection, lastSection, playerPos, firstPos, secondPos);
	}

	public static class Handler {
        public static void handle(final PacketPaste msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
				if(!PlayerAccess.canEdit(player))
					return;
				
				PlayerData data = WorldData.getPlayerData(player);
				
				ArrayList<BlockCache> newUndo = new ArrayList<BlockCache>();
				
				if(msg.firstSection)
					msg.mapPos.put(msg.name, Arrays.asList(msg.firstPos, msg.secondPos, msg.playerPos, new BlockPos(player)));
				
				
				
				int index = msg.firstSection ? 0 : data.getActionStorage().getLastUndo().size();
				
				
				int xDiff = Math.abs(msg.mapPos.get(msg.name).get(0).getX() - msg.mapPos.get(msg.name).get(1).getX()) + 1;
				int yDiff = Math.abs(msg.mapPos.get(msg.name).get(0).getY() - msg.mapPos.get(msg.name).get(1).getY()) + 1;
				int zDiff = Math.abs(msg.mapPos.get(msg.name).get(0).getZ() - msg.mapPos.get(msg.name).get(1).getZ()) + 1;
				
				int x = Math.min(msg.mapPos.get(msg.name).get(0).getX(), msg.mapPos.get(msg.name).get(1).getX());
				int y = Math.min(msg.mapPos.get(msg.name).get(0).getY(), msg.mapPos.get(msg.name).get(1).getY());
				int z = Math.min(msg.mapPos.get(msg.name).get(0).getZ(), msg.mapPos.get(msg.name).get(1).getZ());
				
				BlockPos lowestPos = new BlockPos(x, y, z);
				
				for(BlockCache bse : msg.list) {
					bse.playerPos = msg.firstSection ? msg.playerPos : msg.mapPos.get(msg.name).get(2);
		
					bse.pos = lowestPos.add(index % xDiff, MathHelper.floor((index % (yDiff * xDiff)) / xDiff), MathHelper.floor(index / (yDiff * xDiff)));
					
					index += 1;
				}
				
				for(BlockCache bse : msg.list)
					newUndo.add(bse.restoreRelative(data.getPlayerWorld(), msg.mapPos.get(msg.name).get(3)));
				
				if(msg.firstSection)
					data.getActionStorage().addUndo(newUndo);
				else
					data.getActionStorage().getLastUndo().addAll(newUndo);
				
				if(msg.lastSection) {
					TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.worldtransfer.paste.complete", msg.name);
					chatComponent.getStyle().setColor(TextFormatting.GREEN);
					player.sendMessage(chatComponent);
				}
            }); 

            ctx.get().setPacketHandled(true);
        }
    }
}
