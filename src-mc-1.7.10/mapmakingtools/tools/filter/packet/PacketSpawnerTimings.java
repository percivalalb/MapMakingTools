package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateSpawner;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

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
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.minDelay = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.maxDelay = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.spawnRadius = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.spawnCount = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.entityCap = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.detectionRange = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeStringToBuffer(this.minDelay);
		packetbuffer.writeStringToBuffer(this.maxDelay);
		packetbuffer.writeStringToBuffer(this.spawnRadius);
		packetbuffer.writeStringToBuffer(this.spawnCount);
		packetbuffer.writeStringToBuffer(this.entityCap);
		packetbuffer.writeStringToBuffer(this.detectionRange);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areIntegers(this.minDelay, this.maxDelay, this.spawnRadius, this.spawnCount, this.entityCap, this.detectionRange)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.spawnertimings.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return null;
			}
			
			int minDelayNo = NumberParse.getInteger(this.minDelay);
			int maxDelayNo = NumberParse.getInteger(this.maxDelay);
			int spawnRadiusNo = NumberParse.getInteger(this.spawnRadius);
			int spawnCountNo = NumberParse.getInteger(this.spawnCount);
			int entityCapNo = NumberParse.getInteger(this.entityCap);
			int detectionRadiusNo = NumberParse.getInteger(this.detectionRange);
			
			SpawnerUtil.setMinDelay(spawner.func_145881_a(), minDelayNo);
			SpawnerUtil.setMaxDelay(spawner.func_145881_a(), maxDelayNo);
			SpawnerUtil.setSpawnRadius(spawner.func_145881_a(), spawnRadiusNo);
			SpawnerUtil.setSpawnCount(spawner.func_145881_a(), spawnCountNo);
			SpawnerUtil.setEntityCap(spawner.func_145881_a(), entityCapNo);
			SpawnerUtil.setDetectionRadius(spawner.func_145881_a(), detectionRadiusNo);
			//SpawnerUtil.setTimings(spawner.func_145881_a(), this.minDelay, this.maxDelay, this.spawnRadius, this.spawnCount, this.entityCap, this.detectionRange, this.minecartIndex);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.spawnertimings.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
			return new PacketUpdateSpawner(spawner, this.pos);
		}
		
		return null;
	}
}
