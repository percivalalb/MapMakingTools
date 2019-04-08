package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author ProPercivalalb
 */
public class PacketBiomeUpdate {
	
	public BlockPos pos1, pos2;
	public Biome biome;
	
	public PacketBiomeUpdate(BlockPos pos1, BlockPos pos2, Biome biome) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.biome = biome;
	}
	
	public static void encode(PacketBiomeUpdate msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos1);
		buf.writeBlockPos(msg.pos2);
		buf.writeResourceLocation(ForgeRegistries.BIOMES.getKey(msg.biome));
	}
	
	public static PacketBiomeUpdate decode(PacketBuffer buf) {
		BlockPos pos1 = buf.readBlockPos();
		BlockPos pos2 = buf.readBlockPos();
		Biome biome = ForgeRegistries.BIOMES.getValue(buf.readResourceLocation());
		return new PacketBiomeUpdate(pos1, pos2, biome);
	}
	
	public static class Handler {
        public static void handle(final PacketBiomeUpdate msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(msg.pos1.getX(), 0, msg.pos1.getZ()), new BlockPos(msg.pos2.getX(), 0, msg.pos2.getZ()));
        		
        		for(BlockPos pos : positions) {
        			Chunk chunk = player.world.getChunk(pos);
        			Biome[] biomes = chunk.getBiomes();
        			biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = msg.biome;
        			chunk.setBiomes(biomes);
        			chunk.markDirty();
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}

}
