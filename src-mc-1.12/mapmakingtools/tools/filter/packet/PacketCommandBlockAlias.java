package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCommandBlockAlias extends AbstractServerMessage {

	public BlockPos pos;
	public String name;
	
	public PacketCommandBlockAlias() {}
	public PacketCommandBlockAlias(BlockPos pos, String name) {
		this.pos = pos;
		this.name = name;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.name = packetbuffer.readString(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeString(this.name);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;

		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntityCommandBlock) {
			MapMakingTools.LOGGER.info("YES");
			TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
			commandBlock.getCommandBlockLogic().setName(this.name);
				//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.commandBlockName.complete", name));
				if(ServerHelper.isServer()) {
					//TODO
					
	    			//MinecraftServer server = ServerHelper.mcServer;
	    			//server.getConfigurationManager().sendPacketToAllPlayersInDimension(commandBlock.getDescriptionPacket(), commandBlock.getWorld().provider.getDimensionId());
    			}
    			
    			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.commandblockalias.complete", this.name);
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
			
		}
	}
}
