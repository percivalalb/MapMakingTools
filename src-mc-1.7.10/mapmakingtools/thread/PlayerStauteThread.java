package mapmakingtools.thread;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.datareader.BlockColourList;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class PlayerStauteThread implements Runnable {

	public EntityPlayer player;
	public String target, facing;
	public boolean hat;
	public int multiplyier;
	public PlayerData data;
	
	//imgX, imgY, width, height, imgFlip?, worldX, worldY, worldZ, direction (0 = x, 1 = y, 2 = z), isAffectedByHat
    private Object[][] model = new Object[][] {
    			
    		{40, 20, 4, 12, false, 0, 23, 6, 2}, //Arm Right Right
    		{48, 20, 4, 12, false, 3, 23, 6, 2}, //Arm Right Left
    		{48, 16, 4, 4, false, 0, 12, 9, 1}, //Arm Right Bottom
    		{44, 16, 4, 4, false, 0, 23, 9, 1}, //Arm Right Top
    		{52, 20, 4, 12, false, 0, 23, 9, 0}, //Arm Right Back
    		{44, 20, 4, 12, false, 0, 23, 6, 0}, //Arm Right Front
    			
    		{40, 20, 4, 12, true, 12, 23, 6, 2}, //Arm Left Right
    		{48, 20, 4, 12, true, 15, 23, 6, 2}, //Arm Left Left
    		{48, 16, 4, 4, true, 12, 12, 9, 1}, //Arm Left Bottom
    		{44, 16, 4, 4, true, 12, 23, 9, 1}, //Arm Left Top
    		{52, 20, 4, 12, true, 12, 23, 9, 0}, //Arm Left Back
    		{44, 20, 4, 12, true, 12, 23, 6, 0}, //Arm Left Front
    		
    		{8, 0, 8, 8, true, 4, 31, 11, 1}, //Head Top
    		{16, 0, 8, 8, true, 4, 24, 11, 1}, //Head Bottom
    		
    		{16, 8, 8, 8, true, 4, 31, 4, 2}, //Head Right
    		{0, 8, 8, 8, true, 11, 31, 4, 2}, //Head Left
    		
    		{24, 8, 8, 8, false, 4, 31, 11, 0}, //Head Back
    		{8, 8, 8, 8, true, 4, 31, 4, 0}, //Head Front
    		

			{40, 0, 8, 8, true, 4, 32, 12, 1, true}, //Hat Top
			//{48, 0, 8, 8, true, 11, 24, 4, 1, true}, //Hat Bottom
			
			{48, 8, 8, 8, false, 3, 31, 4, 2, true}, //Hat Right
			{32, 8, 8, 8, true, 12, 31, 4, 2, true}, //Hat Left
    			
			{56, 8, 8, 8, true, 4, 31, 12, 0, true}, //Hat Back
			{40, 8, 8, 8, true, 4, 31, 3, 0, true}, //Hat Front
			
    			
			{20, 16, 8, 4, true, 4, 23, 9, 1}, //Body Top
			{28, 16, 8, 4, false, 4, 12, 9, 1}, //Body Bottom
			
			{16, 20, 4, 12, true, 11, 23, 6, 2}, //Body Right
			{28, 20, 4, 12, false, 4, 23, 6, 2}, //Body Left
			
			{32, 20, 8, 12, true, 4, 23, 9, 0}, //Body Back
			{20, 20, 8, 12, true, 4, 23, 6, 0}, //Body Front
			
			{12, 20, 4, 12, false, 8, 11, 9, 0}, //Legs Left Back
			{12, 20, 4, 12, true, 4, 11, 9, 0}, //Legs Right Back
			{4, 16, 4, 4, true, 4, 11, 9, 1}, //Legs Right Top
			{4, 16, 4, 4, false, 8, 11, 9, 1}, //Legs Left Top
			{8, 16, 4, 4, true, 4, 0, 9, 1}, //Legs Right Bottom
			{8, 16, 4, 4, false, 8, 0, 9, 1}, //Legs Left Bottom
			{8, 20, 4, 12, true, 7, 11, 6, 2}, //Legs Right Right
			{8, 20, 4, 12, false, 8, 11, 6, 2}, //Legs Left Left
			{0, 20, 4, 12, true, 4, 11, 6, 2}, //Legs Right Right
			{0, 20, 4, 12, true, 11, 11, 6, 2}, //Legs Left Left
			{4, 20, 4, 12, true, 8, 11, 6, 0}, //Legs Left Front
			{4, 20, 4, 12, false, 4, 11, 6, 0}, //Legs Right Front
	};
    
    public PlayerStauteThread(PlayerData data, EntityPlayer player, String target, String facing, boolean hat, int multiplyier) {
    	this.data = data;
    	this.player = player;
    	this.target = target;
    	this.facing = facing;
    	this.hat = hat;
    	this.multiplyier = multiplyier;
    }
	
	@Override
	public void run() {
		List<BlockPos> alreadySet = new ArrayList<BlockPos>();
		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
		int blocks = 0;
		
		BufferedImage img = null;
		
		try {
			//URL url = MapMakingTools.class.getResource("/assets/minecraft/textures/entity/steve.png");
			
	    	String urlPath = String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] {target});
	    	URL url = new URL(urlPath);
			img = ImageIO.read(url);
		}
		catch(Exception e) {
			e.printStackTrace();
			ChatComponentTranslation warningChatComponet = new ChatComponentTranslation("mapmakingtools.commands.build.playerstatue.downloaderror");
			warningChatComponet.getChatStyle().setItalic(true);
			warningChatComponet.getChatStyle().setColor(EnumChatFormatting.RED);
			this.player.addChatMessage(warningChatComponet);
		}
		
		ChatComponentTranslation warningChatComponet = new ChatComponentTranslation("mapmakingtools.commands.build.playerstatue.warning");
		warningChatComponet.getChatStyle().setItalic(true);
		warningChatComponet.getChatStyle().setColor(EnumChatFormatting.RED);
		this.player.addChatMessage(warningChatComponet);	
		
    	for(Object[] part : model) {
    		int imgX = (Integer)part[0];
    		int imgY = (Integer)part[1];
    		int width = (Integer)part[2];
    		int height = (Integer)part[3];
    		boolean imgFlip = (Boolean)part[4];
    		int direction = (Integer)part[8];
    		
            for(int i = 0; i < width; ++i) {
            	for(int j = 0; j < height; ++j) {
		    		int worldX = (Integer)part[5];
		    		int worldY = (Integer)part[6];
		    		int worldZ = (Integer)part[7];
            		
		    		if(part.length >= 10)
		    			if((Boolean)part[9] && !this.hat)
		    				continue;
            		
		    		//Is Transparent
	            	if((img.getRGB(imgX + i, imgY + j) >> 24) == 0x00)
	            		continue;
            		
	            	int[] rgb = BlockColourList.getPixelData(img, imgX + i, imgY + j);
	                Object[] closest = BlockColourList.closestMaterial(rgb[0], rgb[1], rgb[2]);
            		
            		if(direction == 0) {
 	                	worldX += (imgFlip ? width - i - 1 : i);
 	                	worldY += -j;
 	                }
            		else if(direction == 1) {
            			worldZ += -j;
 	                	worldX += (imgFlip ? width - i - 1 : i);
            		}
            		else if(direction == 2) {
            			worldZ += (imgFlip ? width - i - 1 : i);
 	                	worldY += -j;
            		}
            		int backUpX = worldX;
            		int backUpZ = worldZ;
            		
            		if(this.facing.equals("east")) {
                		worldX = -backUpZ + 15;
                		worldZ = backUpX;
                	}
                	else if(this.facing.equals("south"))	{
                		worldX = -backUpX + 15;
                		worldZ = -backUpZ + 15;
                	}
                	else if(this.facing.equals("west"))	{
                		worldX = backUpZ;
                		worldZ = -backUpX + 15;
                	}
                	
                	for(int xMult = 0; xMult < this.multiplyier; xMult++) {
                		for(int yMult = 0; yMult < this.multiplyier; yMult++) {
                			for(int zMult = 0; zMult < this.multiplyier; zMult++) {
                				BlockPos newPos = new BlockPos(this.data.getMinX() + worldX * this.multiplyier + xMult, this.data.getMinY() + worldY * multiplyier + yMult, data.getMinZ() + worldZ * multiplyier + zMult);
                				
	    	                	if(!alreadySet.contains(newPos)) {
	    	                		list.add(BlockCache.createCache(this.player, this.player.worldObj, newPos));
	    		     	            alreadySet.add(newPos);
	    		     	            this.player.worldObj.setBlock(newPos.getX(), newPos.getY(), newPos.getZ(), Block.getBlockFromName(((String)closest[0])), (Integer)closest[1], 2);
	    	    				}
	    	     	            
	    	                	++blocks;
                			}
	                	}
                	}
            	}	
	    	}
    	}
		
    	this.data.getActionStorage().addUndo(list);

		ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.playerstatue.complete", target);
		chatComponent.getChatStyle().setItalic(true);
		this.player.addChatMessage(chatComponent);
	}
}
