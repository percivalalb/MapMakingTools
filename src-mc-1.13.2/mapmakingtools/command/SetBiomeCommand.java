package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.command.argument.BiomeArgument;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;

public class SetBiomeCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.setbiome.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/setbiome").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).then(Commands.argument("biome", BiomeArgument.biome())).executes((command) -> {
			return setBiome(command.getSource(), BiomeArgument.getBiome(command, "biome"));
		}));
	}

	private static int setBiome(CommandSource source, Biome biome) throws CommandSyntaxException {
		WorldServer world = source.getWorld();
		Entity entity = source.getEntity();
		
		if(!(entity instanceof EntityPlayer))
			throw ERROR.create();
		
		EntityPlayer player = (EntityPlayer)entity;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw CommandUtil.NO_POINTS_SELECTED.create();
		
		Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), 0, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), 0, data.getSecondPoint().getZ()));
		
		for(BlockPos pos : positions) {
			Chunk chunk = world.getChunk(pos);
			Biome[] biomes = chunk.getBiomes();
			biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = biome;
			chunk.setBiomes(biomes);
			chunk.markDirty();
		}

		PacketHandler.send(PacketDistributor.DIMENSION.with(() -> world.dimension.getType()), new PacketBiomeUpdate(data.getFirstPoint(), data.getSecondPoint(), biome));

		source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.setbiome.success", biome).applyTextStyle(TextFormatting.ITALIC), true);
		return 1;

	}
}