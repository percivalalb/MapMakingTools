package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.core.helper.CommandHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.PotionColourHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.WorldHelper;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;

/**
 * @author ProPercivalalb
 **/
public class CommandWalls extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/walls";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "commands.build.walls.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
    	EntityPlayerMP var3;
    	//Make sure command sender is a player
    	if(par1ICommandSender instanceof EntityPlayerMP) {
        	var3 = (EntityPlayerMP)par1ICommandSender;
    	}
    	else return;
    	
    	if(!DataStorage.hasSelectedPostions(var3)) {
    		throw new CommandException("commands.build.postionsNotSelected", new Object[0]);
    	}
    	
    	if (par2ArrayOfStr.length == 1) {
    		if(!QuickBuildHelper.isValidIds(par2ArrayOfStr[0])) {
    			throw new CommandException("commands.build.blockIdMeta.wrong", new Object[] {par2ArrayOfStr[0]});
    		}
    		int[] values = QuickBuildHelper.convertIdString(par2ArrayOfStr[0]);
    		int blockId = values[0];
    		int blockMeta = values[1];
    		if(blockId != 0) {
    			if(blockId < 0 || blockId > Block.blocksList.length || Block.blocksList[blockId] == null) {
    				throw new CommandException("commands.build.blockId.wrong", new Object[] {String.valueOf(blockId)});
    			}
    		}
    		
    		int secMinX = DataStorage.getSelectedPosFromPlayer(var3)[0];
    		int secMinY = DataStorage.getSelectedPosFromPlayer(var3)[1];
    		int secMinZ = DataStorage.getSelectedPosFromPlayer(var3)[2];
    		int secMaxX = DataStorage.getSelectedPosFromPlayer(var3)[3];
    		int secMaxY = DataStorage.getSelectedPosFromPlayer(var3)[4];
    		int secMaxZ = DataStorage.getSelectedPosFromPlayer(var3)[5]; 
    		int minX = MathHelper.small(secMinX, secMaxX);
    		int minY = MathHelper.small(secMinY, secMaxY);
    		int minZ = MathHelper.small(secMinZ, secMaxZ);
    		int maxX = MathHelper.big(secMinX, secMaxX)+1;
    		int maxY = MathHelper.big(secMinY, secMaxY)+1;
    		int maxZ = MathHelper.big(secMinZ, secMaxZ)+1;
    		
			ArrayList<CachedBlockPlacement> list = new ArrayList<CachedBlockPlacement>();
			int blocks = 0;
			for(int x = minX; x < maxX; ++x) {
				for(int y = maxY - 1; y >= minY; --y) {
					for(int z = minZ; z < maxZ; ++z) {
						boolean flag1 = x == minX;
						boolean flag2 = x == maxX - 1;
						boolean flag3 = z == minZ;
						boolean flag4 = z == maxZ - 1;
						if(flag1 || flag2 || flag3 || flag4) {
							CachedBlockPlacement undo = new CachedBlockPlacement(var3.worldObj, x, y, z);
							list.add(undo);
							WorldHelper.setBlockCheaply(var3.worldObj, x, y, z, blockId, blockMeta);
							++blocks;
						}
					} 
				}
			}
			String blockName = blockId == 0 ? "Air" : new ItemStack(blockId, 1, blockMeta).getDisplayName();
			var3.addChatMessage(blocks + " block(s) have been changed to " + blockName + ".");
			DataStorage.addUndo(list, var3);
    	}
		else {
			throw new WrongUsageException("commands.build.walls.usage", new Object[0]);
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
