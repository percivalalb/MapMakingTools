package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentTranslation;

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
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeStringToBuffer(this.name);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;

		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityCommandBlock) {
			LogHelper.info("YES");
			TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
			commandBlock.func_145993_a().func_145754_b(this.name);
				//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.commandBlockName.complete", name));
				if(ServerHelper.isServer()) {
	    			MinecraftServer server = MinecraftServer.getServer();
	    			server.getConfigurationManager().sendPacketToAllPlayersInDimension(commandBlock.getDescriptionPacket(), commandBlock.getWorldObj().provider.dimensionId);
    			}
				LogHelper.info("GGGG");
    			
    			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.commandblockalias.complete", this.name);
				chatComponent.getChatStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			
		}
		return null;
	}
}
