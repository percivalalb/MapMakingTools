package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;

import com.google.common.base.Strings;

/**
 * @author ProPercivalalb
 */
public class CommandRegenChunks extends CommandBase {

	@Override
	public String getCommandName() {
		return "/regenchunks";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.regenchunks.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayerMP))
			return;
		
		EntityPlayerMP player = (EntityPlayerMP)sender;
		WorldServer world = (WorldServer)player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), 0, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), 0, data.getSecondPoint().getZ()));
			List<Chunk> chunks = new ArrayList<Chunk>();
	
			for(BlockPos pos : positions) {
				Chunk chunk = world.getChunkFromBlockCoords(pos.getX(), pos.getZ());
				if(!chunks.contains(chunk))
					chunks.add(chunk);
			}

			//for(Chunk chunk : chunks) {
			//	 for(int x = 0; x < 16; ++x)
	        //    	   for(int y = 0; y < 256; ++y)
	        //    		   for(int z = 0; z < 16; ++z)
	        //    			   world.setBlock(chunk.xPosition * 16 + x,  y,  chunk.zPosition * 16 + z, Blocks.air, 0, 2);
			//}
			
			for(Chunk chunk : chunks) {
				
				WorldServer worldServer = (WorldServer) world;
				
	            ChunkProviderServer chunkProviderServer = worldServer.theChunkProviderServer;
	            IChunkProvider chunkProviderGenerate = chunkProviderServer.currentChunkProvider;
	            Chunk newChunk = chunkProviderGenerate.provideChunk(chunk.xPosition, chunk.zPosition);
	            newChunk.populateChunk(chunkProviderGenerate, chunkProviderGenerate, chunk.xPosition, chunk.zPosition);
	            
	            ReflectionHelper.setField(Chunk.class, chunk, 2, ReflectionHelper.getField(Chunk.class, ExtendedBlockStorage[].class, newChunk, 2));
	            chunk.setChunkModified();
	            
	            for(int x = 0; x < 16; ++x)
	            	   for(int y = 0; y < 256; ++y)
	            		   for(int z = 0; z < 16; ++z) {
	            			   //Block current = world.getBlock(chunk.xPosition * 16 + x,  y,  chunk.zPosition * 16 + z);
	            			   //Block block  = newChunk.getBlock(x, y, z);
	            			   //if(block != Blocks.air)
	            			   world.markBlockForUpdate(chunk.xPosition * 16 + x,  y,  chunk.zPosition * 16 + z);
	            				   //world.setBlock(chunk.xPosition * 16 + x,  y,  chunk.zPosition * 16 + z, newChunk.getBlock(x, y, z), 0, 2);
	            		   }
	            			  
			}
			
			//MapMakingTools.NETWORK_MANAGER.sendPacketToAllInDimension(new PacketBiomeUpdate(data.getFirstPoint(), data.getSecondPoint(), biome), world.provider.dimensionId);
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.regenchunks.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return null;
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
