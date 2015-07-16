package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketCommandBlockAlias extends IPacketPos {

	public int x, y, z;
	public String name;
	
	public PacketCommandBlockAlias() {}
	public PacketCommandBlockAlias(BlockPos pos, String name) {
		super(pos);
		this.name = name;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeStringToBuffer(this.name);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;

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
	}
}
