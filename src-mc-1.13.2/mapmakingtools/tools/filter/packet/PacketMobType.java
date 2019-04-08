package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketMobType {

	public String mobId;
	public int minecartIndex;
	
	public PacketMobType(String mobId, int minecartIndex) {
		this.mobId = mobId;
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketMobType msg, PacketBuffer buf) {
		buf.writeString(msg.mobId);
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketMobType decode(PacketBuffer buf) {
		String mobId = buf.readString(Integer.MAX_VALUE / 4);
		int minecartIndex = buf.readInt();
		return new PacketMobType(mobId, minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketMobType msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			
        			SpawnerUtil.setMobId(spawnerLogic, msg.mobId, msg.minecartIndex);
        			
        			if(container.getTargetType() == TargetType.BLOCK) {
        				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

        				//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        				PacketUtil.sendTileEntityUpdateToWatching(spawner);
        			}
        			
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.mobType.complete", msg.mobId).applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
