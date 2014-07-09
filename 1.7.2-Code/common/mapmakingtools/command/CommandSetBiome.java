package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;

import cpw.mods.fml.common.FMLLog;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

/**
 * @author ProPercivalalb
 */
public class CommandSetBiome extends CommandBase {

	@Override
	public String getCommandName() {
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
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		WorldServer world = (WorldServer)player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			
			BiomeGenBase biome = getBiomeByText(sender, param[0]);
			
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
					Chunk chunk = world.getChunkFromBlockCoords(x, z);
					byte[] biomes = chunk.getBiomeArray();
					biomes[((z & 0xF) << 4 | x & 0xF)] = (byte)biome.biomeID;
					chunk.setBiomeArray(biomes);
					chunk.setChunkModified();
					for(int i = 0; i < 256; ++i) {
						world.markBlockForUpdate(x, i, z);
					}
				}
			}
			
			List<List<Integer>> list = new ArrayList<List<Integer>>();
			MinecraftServer server = MinecraftServer.getServer();
			
			//Run again to set updates to client
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
					int chunkX = x >> 4;
					int chunkZ = z >> 4;
					List<Integer> ints = Arrays.asList(chunkX, chunkZ);
					if(!list.contains(ints)) {
						server.getConfigurationManager().sendPacketToAllPlayersInDimension(new S21PacketChunkData(world.getChunkFromChunkCoords(chunkX, chunkZ), true, 255), world.provider.dimensionId);
						list.add(ints);
					}
				}
			}
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.setbiome.complete", "" + biome.biomeName);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

	public BiomeGenBase getBiomeByText(ICommandSender sender, String p_147180_1_) {
		BiomeGenBase[] array =  BiomeGenBase.getBiomeGenArray();
		for(int i = 0; i < array.length; ++i) {
			BiomeGenBase biome = array[i];
			if(biome == null) continue;
			String name = biome.biomeName;
			if(!Strings.isNullOrEmpty(name) && name.replaceAll(" ", "").equalsIgnoreCase(p_147180_1_))
				return biome;
		}
		
		try {
			int i = this.parseIntBounded(sender, p_147180_1_, 0, array.length - 1);
            BiomeGenBase biome = array[i];
                
            if (biome != null)
            	return biome;
         }
         catch (NumberFormatException numberformatexception) {}

         throw new NumberInvalidException("mapmakingtools.commands.build.setbiome.notfound", new Object[] {p_147180_1_});

	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, this.getBiomeKeys()) : null;
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
