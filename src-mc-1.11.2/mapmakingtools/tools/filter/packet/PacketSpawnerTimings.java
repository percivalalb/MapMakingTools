package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
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

	public BlockPos pos;
	public String minDelay, maxDelay, spawnRadius, spawnCount, entityCap, detectionRange;
	
	public PacketSpawnerTimings() {}
	public PacketSpawnerTimings(BlockPos pos, String minDelay, String maxDelay, String spawnRadius, String spawnCount, String entityCap, String detectionRange) {
		this.pos = pos;
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.spawnRadius = spawnRadius;
		this.spawnCount = spawnCount;
		this.entityCap = entityCap;
		this.detectionRange = detectionRange;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.minDelay = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.maxDelay = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.spawnRadius = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.spawnCount = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.entityCap = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.detectionRange = packetbuffer.readString(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
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
		
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areIntegers(this.minDelay, this.maxDelay, this.spawnRadius, this.spawnCount, this.entityCap, this.detectionRange)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.spawnertimings.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			int minDelayNo = NumberParse.getInteger(this.minDelay);
			int maxDelayNo = NumberParse.getInteger(this.maxDelay);
			int spawnRadiusNo = NumberParse.getInteger(this.spawnRadius);
			int spawnCountNo = NumberParse.getInteger(this.spawnCount);
			int entityCapNo = NumberParse.getInteger(this.entityCap);
			int detectionRadiusNo = NumberParse.getInteger(this.detectionRange);
			
			SpawnerUtil.setMinDelay(spawner.getSpawnerBaseLogic(), minDelayNo);
			SpawnerUtil.setMaxDelay(spawner.getSpawnerBaseLogic(), maxDelayNo);
			SpawnerUtil.setSpawnRadius(spawner.getSpawnerBaseLogic(), spawnRadiusNo);
			SpawnerUtil.setSpawnCount(spawner.getSpawnerBaseLogic(), spawnCountNo);
			SpawnerUtil.setEntityCap(spawner.getSpawnerBaseLogic(), entityCapNo);
			SpawnerUtil.setDetectionRadius(spawner.getSpawnerBaseLogic(), detectionRadiusNo);
			//SpawnerUtil.setTimings(spawner.getSpawnerBaseLogic(), this.minDelay, this.maxDelay, this.spawnRadius, this.spawnCount, this.entityCap, this.detectionRange, this.minecartIndex);
			
			PacketUtil.sendTileEntityUpdateToWatching(spawner);
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.spawnertimings.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
