package mapmakingtools.command;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mapmakingtools.core.helper.CommandHelper;
import mapmakingtools.core.helper.ImageHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.PotionColourHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.core.util.CachedBlockPlacement;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
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
public class CommandPlayerStatue extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/playerstatue";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "commands.build.playerstatue.usage";
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
    	
    	try {
	    	String urlPath = AbstractClientPlayer.func_110300_d("frodo207");
	    	URL url = new URL(urlPath);
	    	BufferedImage img = ImageIO.read(url);
	    	int[][] pixelData = new int[img.getHeight() * img.getWidth()][3];
	        int[] rgb;

	        int counter = 0;
	        for(int i = 0; i < img.getHeight(); i++){
	            for(int j = 0; j < img.getWidth(); j++){
	            	LogHelper.logInfo("x - " + i + " y - " + j);
	                rgb = ImageHelper.getPixelData(img, i, j);
	                int[] closest = ImageHelper.closestMaterial(rgb[0], rgb[1], rgb[2], 255);
	    	    	LogHelper.logInfo("Block Id: " + closest[0]);
	    	    	LogHelper.logInfo("Block Meta: " + closest[1]);
	                for(int k = 0; k < rgb.length; k++){
	                    pixelData[counter][k] = rgb[k];
	                }

	                counter++;
	            }
	        }
	    	int[] closest = ImageHelper.closestMaterial(var3.worldObj.rand.nextInt(255), var3.worldObj.rand.nextInt(255), var3.worldObj.rand.nextInt(255), 255);
	    	LogHelper.logInfo("Block Id: " + closest[0]);
	    	LogHelper.logInfo("Block Meta: " + closest[1]);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
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
