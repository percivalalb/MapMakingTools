package mapmakingtools.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class PacketUtil {

	public static void sendTileEntityUpdateToWatching(TileEntity tileEntity) {
		if(tileEntity.getWorld() instanceof WorldServer) {
			for(Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				EntityPlayerMP player = (EntityPlayerMP)obj;
				if(((WorldServer)tileEntity.getWorld()).getPlayerManager().isPlayerWatchingChunk(player, tileEntity.getPos().getX() >> 4, tileEntity.getPos().getZ() >> 4));
					player.playerNetServerHandler.sendPacket(tileEntity.getDescriptionPacket());
			}
		}
	}

}
