package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketSignEdit extends AbstractServerMessage {

	public BlockPos pos;
	public ITextComponent[] signLines;
	
	public PacketSignEdit() {}
	public PacketSignEdit(BlockPos pos, ITextComponent[] par4ArrayOfStr) {
		this.pos = pos;
		this.signLines = par4ArrayOfStr;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		this.signLines = new ITextComponent[4];
		this.pos = buffer.readBlockPos();
        for (int i = 0; i < 4; ++i)
            this.signLines[i] = buffer.readTextComponent();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(this.pos);
		for (int i = 0; i < 4; ++i)
			buffer.writeTextComponent(this.signLines[i]);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)tile;
			for(int i = 0; i < 4; i++)
				sign.signText[i] = this.signLines[i];
			Chunk chunk = player.world.getChunkFromBlockCoords(this.pos);
			if(chunk != null)
				chunk.setChunkModified();
				
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.signedit.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
			
			PacketUtil.sendTileEntityUpdateToWatching(sign);
		}
	}
}