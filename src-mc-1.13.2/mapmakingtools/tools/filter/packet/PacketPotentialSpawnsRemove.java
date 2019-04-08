package mapmakingtools.tools.filter.packet;

import java.util.List;
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
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketPotentialSpawnsRemove {

	public int minecartIndex;
	
	public PacketPotentialSpawnsRemove(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketPotentialSpawnsRemove msg, PacketBuffer buf) {
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketPotentialSpawnsRemove decode(PacketBuffer buf) {
		int minecartIndex = buf.readInt();
		return new PacketPotentialSpawnsRemove(minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketPotentialSpawnsRemove msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawnerLogic);
        			
        			minecarts.remove(msg.minecartIndex);
        			spawnerLogic.setNextSpawnData((WeightedSpawnerEntity)WeightedRandom.getRandomItem(spawnerLogic.getWorld().rand, minecarts));
        			
        			if(container.getTargetType() == TargetType.BLOCK) {
        				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
        				
        				//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        				PacketUtil.sendTileEntityUpdateToWatching(spawner);
        			}
        			
        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.removeIndex");
        			chatComponent.getStyle().setItalic(true);
        			player.sendMessage(chatComponent);
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
