package mapmakingtools.util;

import mapmakingtools.helper.ServerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class PacketUtil {

	public static void sendTileEntityUpdateToWatching(TileEntity tileEntity) {
		if(tileEntity.getWorld() instanceof WorldServer) {
			for(EntityPlayerMP player : ServerHelper.getServer().getPlayerList().getPlayers()) {
				if(((WorldServer)tileEntity.getWorld()).getPlayerChunkMap().isPlayerWatchingChunk(player, tileEntity.getPos().getX() >> 4, tileEntity.getPos().getZ() >> 4));
					player.connection.sendPacket(tileEntity.getUpdatePacket());
			}
		}
	}

}
