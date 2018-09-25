package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.helper.Numbers;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketSpawnerTimings extends AbstractServerMessage {

	public String minDelay, maxDelay, spawnRadius, spawnCount, entityCap, detectionRange;
	
	public PacketSpawnerTimings() {}
	public PacketSpawnerTimings(String minDelay, String maxDelay, String spawnRadius, String spawnCount, String entityCap, String detectionRange) {
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.spawnRadius = spawnRadius;
		this.spawnCount = spawnCount;
		this.entityCap = entityCap;
		this.detectionRange = detectionRange;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.minDelay = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.maxDelay = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.spawnRadius = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.spawnCount = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.entityCap = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.detectionRange = packetbuffer.readString(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.minDelay);
		packetbuffer.writeString(this.maxDelay);
		packetbuffer.writeString(this.spawnRadius);
		packetbuffer.writeString(this.spawnCount);
		packetbuffer.writeString(this.entityCap);
		packetbuffer.writeString(this.detectionRange);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
			
			if(!Numbers.areIntegers(this.minDelay, this.maxDelay, this.spawnRadius, this.spawnCount, this.entityCap, this.detectionRange)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.spawnertimings.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			int minDelayNo = Numbers.parse(this.minDelay);
			int maxDelayNo = Numbers.parse(this.maxDelay);
			int spawnRadiusNo = Numbers.parse(this.spawnRadius);
			int spawnCountNo = Numbers.parse(this.spawnCount);
			int entityCapNo = Numbers.parse(this.entityCap);
			int detectionRadiusNo = Numbers.parse(this.detectionRange);
			
			SpawnerUtil.setMinDelay(spawnerLogic, minDelayNo);
			SpawnerUtil.setMaxDelay(spawnerLogic, maxDelayNo);
			SpawnerUtil.setSpawnRadius(spawnerLogic, spawnRadiusNo);
			SpawnerUtil.setSpawnCount(spawnerLogic, spawnCountNo);
			SpawnerUtil.setEntityCap(spawnerLogic, entityCapNo);
			SpawnerUtil.setDetectionRadius(spawnerLogic, detectionRadiusNo);
			
			if(container.getTargetType() == TargetType.BLOCK) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

				PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
				PacketUtil.sendTileEntityUpdateToWatching(spawner);
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.spawnertimings.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
