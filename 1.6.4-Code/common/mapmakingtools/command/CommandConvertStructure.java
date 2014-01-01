package mapmakingtools.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import mapmakingtools.MapMakingTools;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.DirectoryHelper;
import mapmakingtools.core.helper.LookHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.core.util.CachedBlockPlacement;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketFly;
import mapmakingtools.network.packet.PacketSkullModify;
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
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 **/
public class CommandConvertStructure extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/convert-to-java";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.convertToJava.usage";
    }

    public void processCommand(ICommandSender sender, String[] param) {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		if(!DataStorage.hasSelectedPostions(player)) {
        		throw new CommandException("commands.build.postionsNotSelected", new Object[0]);
        	}
        	
        	if (param.length == 1) {
        		
        		
        		String fileName = "StructureCreator";
        		fileName = String.valueOf(param[0]);
    		
        		try {
        			File file = new File(DirectoryHelper.mcDir, fileName + ".java");
        			int count = 1;
        			while(file.exists()) {
        				fileName += count;
        				count++;
        				file = new File(DirectoryHelper.mcDir, fileName + ".java");
        			}
        			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    			
        			write("package your.mod.pack;", writer);
    				writeEmpty(writer);
    				write("import java.util.Random;", writer);
    				writeEmpty(writer);
    				write("import net.minecraft.world.World;", writer);
    				write("import net.minecraft.world.gen.feature.WorldGenerator;", writer);
    				writeEmpty(writer);
    				write("/**", writer);
    				write(" * File created at " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date()), writer);
    				write(" * This is an auto-generated java file by Map Making Tools", writer);
    				write(" * There are still some errors you will need to fix in this", writer);
    				write(" * file before is can be used in a mod.", writer);
    				write(" * @author ProPercivalalb", writer);
    				write(" */", writer);
    				write("public class " + fileName + " extends WorldGenerator {", writer);
    				writeEmpty(writer);
    				//Class Writing (Start)
    				write("	@Override", writer);
	    			write("	public boolean generate(World world, Random random, int x, int y, int z) {", writer);
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
	        		
	    			for(int x = minX; x < maxX; ++x) {
	    				for(int y = minY; y < maxY; ++y) {
	    					for(int z = minZ; z < maxZ; ++z) {
	    						CachedBlockPlacement cache = new CachedBlockPlacement(player.worldObj, x, y, z, player);
	    						int txtX = cache.x();
	    						int txtY = cache.y();
	    						int txtZ = cache.x();
	    						if(cache.getTileEntity() != null) {
	    							
	    						}
	    						write("		world.setBlock(x + " + txtX + ", y + " + txtY + ", z + " + txtZ + ", " + cache.getBlockId() + ", " + cache.getBlockMeta() + ", 3);", writer);
	    					} 
	    				}
	    			}
	    			
	    			write("		return true;", writer);
	    			write("	}", writer);
	    			//Class Writing (End)
	    			writeEmpty(writer);
	    			write("}", writer);
    			
	    			writer.close();
    			
	    			if(!file.exists())
	    				file.createNewFile();
    			
        		} 
        		catch(Exception e) {
        			//player.addChatMessage(");
        			e.printStackTrace();
        		}
        	}
        	else {
    			throw new WrongUsageException("commands.convertToJava.usage", new Object[0]);
    	    }
    	}
    }
    
    public void write(String txt, BufferedWriter writer) {
    	try {
			writer.write(txt + "\r");
		} 
    	catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    public void writeEmpty(BufferedWriter writer) {
    	this.write("", writer);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] par2ArrayOfStr) {
    	return null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
