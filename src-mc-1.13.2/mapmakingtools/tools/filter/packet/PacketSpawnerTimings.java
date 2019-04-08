package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.helper.Numbers;
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
public class PacketSpawnerTimings {

	public String minDelay, maxDelay, spawnRadius, spawnCount, entityCap, detectionRange;
	
	public PacketSpawnerTimings(String minDelay, String maxDelay, String spawnRadius, String spawnCount, String entityCap, String detectionRange) {
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.spawnRadius = spawnRadius;
		this.spawnCount = spawnCount;
		this.entityCap = entityCap;
		this.detectionRange = detectionRange;
	}
	
	public static void encode(PacketSpawnerTimings msg, PacketBuffer buf) {
		buf.writeString(msg.minDelay);
		buf.writeString(msg.maxDelay);
		buf.writeString(msg.spawnRadius);
		buf.writeString(msg.spawnCount);
		buf.writeString(msg.entityCap);
		buf.writeString(msg.detectionRange);
	}
	
	public static PacketSpawnerTimings decode(PacketBuffer buf) {
		String minDelay = buf.readString(Integer.MAX_VALUE / 4);
		String maxDelay = buf.readString(Integer.MAX_VALUE / 4);
		String spawnRadius = buf.readString(Integer.MAX_VALUE / 4);
		String spawnCount = buf.readString(Integer.MAX_VALUE / 4);
		String entityCap = buf.readString(Integer.MAX_VALUE / 4);
		String detectionRange = buf.readString(Integer.MAX_VALUE / 4);
		return new PacketSpawnerTimings(minDelay, maxDelay, spawnRadius, spawnCount, entityCap, detectionRange);
	}
	
	public static class Handler {
        public static void handle(final PacketSpawnerTimings msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			
        			if(!Numbers.areIntegers(msg.minDelay, msg.maxDelay, msg.spawnRadius, msg.spawnCount, msg.entityCap, msg.detectionRange)) {
        				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.spawnertimings.notint");
        				chatComponent.getStyle().setItalic(true);
        				chatComponent.getStyle().setColor(TextFormatting.RED);
        				player.sendMessage(chatComponent);
        				return;
        			}
        			
        			int minDelayNo = Numbers.parse(msg.minDelay);
        			int maxDelayNo = Numbers.parse(msg.maxDelay);
        			int spawnRadiusNo = Numbers.parse(msg.spawnRadius);
        			int spawnCountNo = Numbers.parse(msg.spawnCount);
        			int entityCapNo = Numbers.parse(msg.entityCap);
        			int detectionRadiusNo = Numbers.parse(msg.detectionRange);
        			
        			SpawnerUtil.setMinDelay(spawnerLogic, minDelayNo);
        			SpawnerUtil.setMaxDelay(spawnerLogic, maxDelayNo);
        			SpawnerUtil.setSpawnRadius(spawnerLogic, spawnRadiusNo);
        			SpawnerUtil.setSpawnCount(spawnerLogic, spawnCountNo);
        			SpawnerUtil.setEntityCap(spawnerLogic, entityCapNo);
        			SpawnerUtil.setDetectionRadius(spawnerLogic, detectionRadiusNo);
        			
        			if(container.getTargetType() == TargetType.BLOCK) {
        				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

        				//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        				PacketUtil.sendTileEntityUpdateToWatching(spawner);
        			}
        			
        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.spawnertimings.complete");
        			chatComponent.getStyle().setItalic(true);
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.spawnertimings.complete").applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
