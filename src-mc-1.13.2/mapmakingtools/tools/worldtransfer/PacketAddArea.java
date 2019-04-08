package mapmakingtools.tools.worldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import mapmakingtools.client.gui.button.GuiHorizontalTab.Side;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.filter.packet.PacketCommandBlockAlias;
import mapmakingtools.util.CommandBlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketAddArea {

	public String name;
	public ArrayList<BlockCache> list;
	public boolean firstSection, lastSection;
	
	public BlockPos playerPos, firstPos, secondPos;
	
	public PacketAddArea(String name, ArrayList<BlockCache> list, boolean firstSection, boolean lastSection, BlockPos playerPos, BlockPos firstPos, BlockPos secondPos) {
		this.name = name;
		this.list = list;
		this.firstSection = firstSection;
		this.lastSection = lastSection;
		this.playerPos = playerPos;
		this.firstPos = firstPos;
		this.secondPos = secondPos;
	}
	
	public PacketAddArea(String name, ArrayList<BlockCache> list, boolean firstSection, boolean lastSection, PlayerData data) {
		this(name, list, firstSection, lastSection, list.get(0).playerPos, data.getFirstPoint(), data.getSecondPoint());
	}
	
	public static void encode(PacketAddArea msg, PacketBuffer buf) {
		buf.writeString(msg.name);
		buf.writeBoolean(msg.firstSection);
		buf.writeBoolean(msg.lastSection);
		if(msg.firstSection) {
			buf.writeBlockPos(msg.playerPos);
			buf.writeBlockPos(msg.firstPos);
			buf.writeBlockPos(msg.secondPos);
		}
		
		buf.writeInt(msg.list.size());
		for(int i = 0; i < msg.list.size(); ++i)
			msg.list.get(i).writeToPacketBufferCompact(buf);
	}
	
	public static PacketAddArea decode(PacketBuffer buf) {
		String name = buf.readString(Integer.MAX_VALUE / 4);
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
		
		ArrayList <BlockCache> list = new ArrayList<BlockCache>();
		int size = buf.readInt();
		for(int i = 0; i < size; ++i)
			list.add(BlockCache.readFromPacketBufferCompact(buf));
		return new PacketAddArea(name, list, firstSection, lastSection, playerPos, firstPos, secondPos);
	}
	
	public static class Handler {
        public static void handle(final PacketAddArea msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(msg.firstSection)
        			WorldTransferList.mapPos.put(msg.name, Arrays.asList(msg.firstPos, msg.secondPos));
        		
        		int index = msg.firstSection ? 0 : WorldTransferList.getAreaFromName(msg.name).size();
        		
        		int xDiff = Math.abs(WorldTransferList.mapPos.get(msg.name).get(0).getX() - WorldTransferList.mapPos.get(msg.name).get(1).getX()) + 1;
        		int yDiff = Math.abs(WorldTransferList.mapPos.get(msg.name).get(0).getY() - WorldTransferList.mapPos.get(msg.name).get(1).getY()) + 1;
        		int zDiff = Math.abs(WorldTransferList.mapPos.get(msg.name).get(0).getZ() - WorldTransferList.mapPos.get(msg.name).get(1).getZ()) + 1;
        		
        		int x = Math.min(WorldTransferList.mapPos.get(msg.name).get(0).getX(), WorldTransferList.mapPos.get(msg.name).get(1).getX());
        		int y = Math.min(WorldTransferList.mapPos.get(msg.name).get(0).getY(), WorldTransferList.mapPos.get(msg.name).get(1).getY());
        		int z = Math.min(WorldTransferList.mapPos.get(msg.name).get(0).getZ(), WorldTransferList.mapPos.get(msg.name).get(1).getZ());
        		
        		BlockPos lowestPos = new BlockPos(x, y, z);
        		
        		for(BlockCache bse : msg.list) {
        			bse.playerPos = msg.firstSection ? msg.playerPos : WorldTransferList.getAreaFromName(msg.name).get(0).playerPos;

        			bse.pos = lowestPos.add(index % xDiff, MathHelper.floor((index % (yDiff * xDiff)) / xDiff), MathHelper.floor(index / (yDiff * xDiff)));
        			
        			index += 1;
        		}
        		
        		WorldTransferList.put(msg.name, msg.firstSection, msg.lastSection, msg.list);

        		if(msg.lastSection) {
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete", msg.name).applyTextStyle(TextFormatting.GREEN));
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete2", msg.name).applyTextStyle(TextFormatting.ITALIC));
        			
        			WorldTransferList.saveToFile();
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
