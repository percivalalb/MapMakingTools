package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mapmakingtools.core.helper.CommandHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.PotionColourHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.CachedBlockPlacement;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.WeightedRandomMinecart;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

/**
 * @author ProPercivalalb
 **/
public class CommandCombineSpawners extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/combinespawners";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "commands.build.combinespawners.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
    	EntityPlayerMP player;
    	World world;
    	//Make sure command sender is a player
    	if(par1ICommandSender instanceof EntityPlayerMP) {
        	player = (EntityPlayerMP)par1ICommandSender;
        	world = player.worldObj;
    	}
    	else return;
    	
    	if(!DataStorage.hasSelectedPostions(player)) {
    		throw new CommandException("commands.build.postionsNotSelected", new Object[0]);
    	}
    	
    	int secMinX = DataStorage.getSelectedPosFromPlayer(player)[0];
    	int secMinY = DataStorage.getSelectedPosFromPlayer(player)[1];
    	int secMinZ = DataStorage.getSelectedPosFromPlayer(player)[2];
    	int secMaxX = DataStorage.getSelectedPosFromPlayer(player)[3];
    	int secMaxY = DataStorage.getSelectedPosFromPlayer(player)[4];
    	int secMaxZ = DataStorage.getSelectedPosFromPlayer(player)[5]; 
    	int minX = MathHelper.small(secMinX, secMaxX);
    	int minY = MathHelper.small(secMinY, secMaxY);
    	int minZ = MathHelper.small(secMinZ, secMaxZ);
    	int maxX = MathHelper.big(secMinX, secMaxX)+1;
    	int maxY = MathHelper.big(secMinY, secMaxY)+1;
    	int maxZ = MathHelper.big(secMinZ, secMaxZ)+1;

		
		try {
			TileEntityMobSpawner combinedSpawner = new TileEntityMobSpawner();
			MobSpawnerBaseLogic spawnerLogic = combinedSpawner.getSpawnerLogic();
			List<WeightedRandomMinecart> randomlist = new ArrayList<WeightedRandomMinecart>();
			for(int x = minX; x < maxX; ++x) {
				for(int y = minY; y < maxY; ++y) {
					for(int z = minZ; z < maxZ; ++z) {
						TileEntity tile = world.getBlockTileEntity(x, y, z);
						if(tile instanceof TileEntityMobSpawner) {
							randomlist.add(SpawnerHelper.getWeightedMinecart(tile));
						}
					} 
				}
			}
			SpawnerHelper.setWeightedMinecart(combinedSpawner, randomlist);
			ArrayList<CachedBlockPlacement> list = new ArrayList<CachedBlockPlacement>();
			list.add(new CachedBlockPlacement(world, combinedSpawner, 0, 0, 0));
			DataStorage.addCopy(list, player);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new CommandException("commands.build.chunkDoesNotExist", new Object[0]);
		}
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
    	switch(par2ArrayOfStr.length) {
    		case 1: 
    			return null;
    	}
    	return null;
    }


    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
