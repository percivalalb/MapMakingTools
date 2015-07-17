package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
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
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.setbiome.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayerMP))
			return;
		
		EntityPlayerMP player = (EntityPlayerMP)sender;
		WorldServer world = (WorldServer)player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			
			BiomeGenBase biome = getBiomeByText(sender, param[0]);
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), 0, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), 0, data.getSecondPoint().getZ()));
			
			for(BlockPos pos : positions) {
				Chunk chunk = world.getChunkFromBlockCoords(pos);
				byte[] biomes = chunk.getBiomeArray();
				biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = (byte)biome.biomeID;
				chunk.setBiomeArray(biomes);
				chunk.setChunkModified();
			}
			
			PacketDispatcher.sendToDimension(new PacketBiomeUpdate(data.getFirstPoint(), data.getSecondPoint(), biome), world.provider.getDimensionId());
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.setbiome.complete", "" + biome.biomeName);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

	public BiomeGenBase getBiomeByText(ICommandSender sender, String p_147180_1_) throws CommandException {
		BiomeGenBase[] array =  BiomeGenBase.getBiomeGenArray();
		for(int i = 0; i < array.length; ++i) {
			BiomeGenBase biome = array[i];
			if(biome == null) continue;
			String name = biome.biomeName;
			if(!Strings.isNullOrEmpty(name) && name.replaceAll(" ", "").equalsIgnoreCase(p_147180_1_))
				return biome;
		}
		
		try {
			int i = this.parseInt(p_147180_1_, 0, array.length - 1);
            BiomeGenBase biome = array[i];
                
            if (biome != null)
            	return biome;
         }
         catch (NumberFormatException numberformatexception) {}

         throw new NumberInvalidException("mapmakingtools.commands.build.setbiome.notfound", new Object[] {p_147180_1_});

	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return par2ArrayOfStr.length == 1 ? func_175762_a(par2ArrayOfStr, this.getBiomeKeys()) : null;
    }

    private List<String> getBiomeKeys() {
		List<String> list = new ArrayList<String>();
		BiomeGenBase[] array =  BiomeGenBase.getBiomeGenArray();
		for(int i = 0; i < array.length; ++i) {
			BiomeGenBase biome = array[i];
			if(biome == null) continue;
			String name = biome.biomeName.replaceAll(" ", "").toLowerCase();
			if(!Strings.isNullOrEmpty(name))
				list.add(name);
		}
		return list;
	}

	@Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
