package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.core.helper.CommandHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.PotionColourHelper;
import mapmakingtools.lib.NBTData;
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
public class CommandPotionCreator extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "potion";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "commands.potion.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
    	EntityPlayerMP var3;
    	//Make sure command sender is a player
    	if(par1ICommandSender instanceof EntityPlayerMP) {
        	var3 = (EntityPlayerMP)par1ICommandSender;
    	}
    	else return;
    	
    	if (par2ArrayOfStr.length >= 2) {
        	
        	//Check for Potion in hand
            if(!ItemStackHelper.isItem(var3.getHeldItem(), Item.potion)) {
            	throw new CommandException("commands.potion.handHeldItem", new Object[0]);
            }
        	
        	String mode = par2ArrayOfStr[0];
            int id = CommandHelper.getPotionIdFromString(par2ArrayOfStr[1]);
            int duration = 0;
            int level = 0;
            
            if(!mode.equalsIgnoreCase("set") && !mode.equalsIgnoreCase("add") && !mode.equalsIgnoreCase("clear")) {
            	throw new CommandException("commands.potion.modeNotFound", new Object[] {mode});
            }

            if (id == -1 || Potion.potionTypes[id] == null) {
                throw new NumberInvalidException("commands.potion.notFound", new Object[] {Integer.valueOf(id)});
            }
            else {
                if (par2ArrayOfStr.length >= 3) {
                    level = parseIntBounded(par1ICommandSender, par2ArrayOfStr[2], 1, 9999999);
                }
                else {
                	throw new WrongUsageException("commands.potion.usage", new Object[0]);
                }

                if (par2ArrayOfStr.length >= 4) {
                    duration = parseIntBounded(par1ICommandSender, par2ArrayOfStr[3], 0, 9999999);
                }
                else {
                	throw new WrongUsageException("commands.potion.usage", new Object[0]);
                }
                
                int metadata = var3.getHeldItem().getItemDamage();

                if(par2ArrayOfStr.length == 5) {
                	String type = par2ArrayOfStr[4];
                    if(type.equalsIgnoreCase("splash")) {
                    	metadata = PotionColourHelper.getSplashIdFromString("unknown");
                    }
                    else if(type.equalsIgnoreCase("drink")) {
                    	metadata = PotionColourHelper.getBottleIdFromString("unknown");
                    }
                }
                
                if(par2ArrayOfStr.length >= 6) {
                	String type = par2ArrayOfStr[4];
                	String colour = par2ArrayOfStr[5];
                	 if(type.equalsIgnoreCase("splash")) {
                     	metadata = PotionColourHelper.getSplashIdFromString(colour);
                     }
                     else if(type.equalsIgnoreCase("drink")) {
                     	metadata = PotionColourHelper.getBottleIdFromString(colour);
                     }
                	
                }

                //Make sure itemstack has a #NBTTagCompound
                ItemStackHelper.makeSureItemHasTagCompound(var3.getHeldItem());
                
			    if (mode.equalsIgnoreCase("set") || !ItemStackHelper.hasTag(var3.getHeldItem(), NBTData.POTION_TAG)) {
			    	ItemStackHelper.setTag(var3.getHeldItem(), NBTData.POTION_TAG, new NBTTagList());
			    }

			    //Set the Potion Effects
			    ItemStackHelper.setPotionEffects(var3.getHeldItem(), id, level, duration, true);
			    var3.getHeldItem().setItemDamage(metadata);
			    
                notifyAdmins(par1ICommandSender, "commands.potion.success", new Object[] {var3.getEntityName()});
            }
        }
        else if (par2ArrayOfStr.length == 1) {
        	ItemStackHelper.setTag(var3.getHeldItem(), NBTData.POTION_TAG, new NBTTagList());
        }
        else {
            throw new WrongUsageException("commands.potion.usage", new Object[0]);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
    	switch(par2ArrayOfStr.length) {
    		case 1: 
    			return getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getMode());
    		case 2: 
    			return getListOfStringsMatchingLastWord(par2ArrayOfStr, CommandHelper.getPotionNames());
    		case 5: 
    			return getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPotionTypes());
    		case 6: 
    			return getListOfStringsMatchingLastWord(par2ArrayOfStr, PotionColourHelper.getPotionColours());
    		default: 
    			return null;
    	}
    }
   
    protected String[] getMode() {
    	return new String[] {"add", "set", "clear"};
    }
    
    protected String[] getPotionTypes() {
    	return new String[] {"splash", "drink"};
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
