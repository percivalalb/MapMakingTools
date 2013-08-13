package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.core.helper.CommandHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.PotionColourHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketFly;
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
public class CommandRotation extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/rotate";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.build.rotate.usage";
    }

    public void processCommand(ICommandSender sender, String[] param) {
    	EntityPlayerMP player;
    	//Make sure command sender is a player
    	if(sender instanceof EntityPlayerMP) {
    		player = (EntityPlayerMP)sender;
    	}
    	else return;
    	
    	if(!DataStorage.hasCopy(player)) {
    		throw new CommandException("commands.build.nothingToRotate", new Object[0]);
    	}
  
    	if(param.length == 1) {
			String rotationString = param[0];
			int rotation = parseIntBounded(sender, rotationString, 0, 270);
			DataStorage.setRotation(player, rotation);
		}
		else {
			throw new WrongUsageException("commands.build.rotate.usage", new Object[0]);
	    }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] param) {
    	switch(param.length) {
    		case 1: 
    			return getListOfStringsMatchingLastWord(param, "90", "180", "270");
    	}
    	return null;
    }


    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
