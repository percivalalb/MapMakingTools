package mapmakingtools.command;

import java.util.Collections;
import java.util.List;

import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

/**
 * @author ProPercivalalb
 */
public class CommandSetBiome extends CommandBase {

	@Override
	public String getName() {
		return "/setbiome";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.setbiome.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayerMP))
			return;
		
		EntityPlayerMP player = (EntityPlayerMP)sender;
		WorldServer world = (WorldServer)player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(args.length < 1)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			
			Biome biome = getBiomeByText(sender, args[0]);
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), 0, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), 0, data.getSecondPoint().getZ()));
			
			for(BlockPos pos : positions) {
				Chunk chunk = world.getChunkFromBlockCoords(pos);
				byte[] biomes = chunk.getBiomeArray();
				biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = (byte)Biome.getIdForBiome(biome);
				chunk.setBiomeArray(biomes);
				chunk.markDirty();
			}
			
			PacketDispatcher.sendToDimension(new PacketBiomeUpdate(data.getFirstPoint(), data.getSecondPoint(), biome), world.provider.getDimension());
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.setbiome.complete", "" + biome.getBiomeName());
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

    public static Biome getBiomeByText(ICommandSender sender, String id) throws NumberInvalidException {
        ResourceLocation resourcelocation = new ResourceLocation(id);

        if (!Biome.REGISTRY.containsKey(resourcelocation))
            throw new NumberInvalidException("mapmakingtools.commands.build.setbiome.notfound", new Object[] {resourcelocation});
        else
            return (Biome)Biome.REGISTRY.getObject(resourcelocation);
    }
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, Biome.REGISTRY.getKeys()) : Collections.<String>emptyList();
	}

}
