package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketSignEdit extends AbstractServerMessage {

	public BlockPos pos;
	public IChatComponent[] signLines;
	
	public PacketSignEdit() {}
	public PacketSignEdit(BlockPos pos, IChatComponent[] par4ArrayOfStr) {
		this.pos = pos;
		this.signLines = par4ArrayOfStr;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		this.signLines = new IChatComponent[4];
		this.pos = buffer.readBlockPos();
        for (int i = 0; i < 4; ++i)
            this.signLines[i] = buffer.readChatComponent();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(this.pos);
		for (int i = 0; i < 4; ++i)
			buffer.writeChatComponent(this.signLines[i]);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(tile instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)tile;
			for(int i = 0; i < 4; i++)
				sign.signText[i] = this.signLines[i];
			Chunk chunk = player.worldObj.getChunkFromBlockCoords(this.pos);
			if(chunk != null)
				chunk.setChunkModified();
				
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.signedit.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
			PacketUtil.sendTileEntityUpdateToWatching(sign);
		}
	}
}