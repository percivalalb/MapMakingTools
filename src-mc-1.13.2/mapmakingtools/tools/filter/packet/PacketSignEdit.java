package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketSignEdit {

	public BlockPos pos;
	public ITextComponent[] signLines;
	
	public PacketSignEdit(BlockPos pos, ITextComponent[] par4ArrayOfStr) {
		this.pos = pos;
		this.signLines = par4ArrayOfStr;
	}
	
	public static void encode(PacketSignEdit msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
		for (int i = 0; i < 4; ++i)
			buf.writeTextComponent(msg.signLines[i]);
	}
	
	public static PacketSignEdit decode(PacketBuffer buf) {
		ITextComponent[] signLines = new ITextComponent[4];
		BlockPos pos = buf.readBlockPos();
        for(int i = 0; i < 4; ++i)
            signLines[i] = buf.readTextComponent();
		return new PacketSignEdit(pos, signLines);
	}
	
	public static class Handler {
        public static void handle(final PacketSignEdit msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		TileEntity tile = player.world.getTileEntity(msg.pos);
        		if(tile instanceof TileEntitySign) {
        			TileEntitySign sign = (TileEntitySign)tile;
        			for(int i = 0; i < 4; i++)
        				sign.signText[i] = msg.signLines[i];
        			Chunk chunk = player.world.getChunk(msg.pos);
        			if(chunk != null)
        				chunk.markDirty();
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.signedit.complete").applyTextStyle(TextFormatting.ITALIC));
        			
        			PacketUtil.sendTileEntityUpdateToWatching(sign);
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}