package mapmakingtools.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.tools.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

/**
 * @author ProPercivalalb
 */
public class PacketBiomeUpdate extends AbstractClientMessage {
	
	public BlockPos pos1, pos2;
	public BiomeGenBase biome;
	
	public PacketBiomeUpdate() {}
	public PacketBiomeUpdate(BlockPos pos1, BlockPos pos2, BiomeGenBase biome) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.biome = biome;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos1 = BlockPos.fromLong(packetbuffer.readLong());
		this.pos2 = BlockPos.fromLong(packetbuffer.readLong());
		this.biome = BiomeGenBase.getBiomeGenArray()[packetbuffer.readInt()];
	}
	
	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos1.toLong());
		packetbuffer.writeLong(this.pos2.toLong());
		packetbuffer.writeInt(this.biome.biomeID);
	}
	
	@Override
	public IMessage process(EntityPlayer player, Side side) {
		Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(this.pos1.getX(), 0, this.pos1.getZ()), new BlockPos(this.pos2.getX(), 0, this.pos2.getZ()));
		
		for(BlockPos pos : positions) {
			Chunk chunk = player.worldObj.getChunkFromBlockCoords(pos.getX(), pos.getZ());
			byte[] biomes = chunk.getBiomeArray();
			biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = (byte)this.biome.biomeID;
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
		}
		return null;
	}

}
