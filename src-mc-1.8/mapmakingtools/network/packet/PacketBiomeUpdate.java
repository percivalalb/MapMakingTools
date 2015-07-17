package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;

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
		this.pos1 = packetbuffer.readBlockPos();
		this.pos2 = packetbuffer.readBlockPos();
		this.biome = BiomeGenBase.getBiomeGenArray()[packetbuffer.readInt()];
	}
	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos1);
		packetbuffer.writeBlockPos(this.pos2);
		packetbuffer.writeInt(this.biome.biomeID);
	}
	
	@Override
	public void process(EntityPlayer player, Side side) {
		Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(this.pos1.getX(), 0, this.pos1.getZ()), new BlockPos(this.pos2.getX(), 0, this.pos2.getZ()));
		
		for(BlockPos pos : positions) {
			Chunk chunk = player.worldObj.getChunkFromBlockCoords(pos);
			byte[] biomes = chunk.getBiomeArray();
			biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = (byte)this.biome.biomeID;
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
		}
	}

}
