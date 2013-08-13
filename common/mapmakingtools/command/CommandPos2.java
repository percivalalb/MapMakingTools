package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.LookHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.proxy.CommonProxy;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 **/
public class CommandPos2 extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/pos2";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.build.pos2.usage";
    }

    public void processCommand(ICommandSender sender, String[] param) {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		int x = MathHelper.floor_double(player.posX);
    		int y = MathHelper.floor_double(player.posY);
    		int z = MathHelper.floor_double(player.posZ);
    		if(param.length == 3) {
    			try {
    				x = new Integer(param[0]);
    				y = new Integer(param[1]);
    				z = new Integer(param[2]);
    			}
    			catch(Exception e) {
    				throw new WrongUsageException("commands.build.pos2.usage", new Object[0]);
    			}
    		}
    		else if(param.length != 0) {
    			throw new WrongUsageException("commands.build.pos2.usage", new Object[0]);
    		}
    		DataStorage.setPlayerRightClick(player, x, y, z);
    		String message = "Postion 2 set at (" + x + ", " + y + ", " + z + ")";
			if(DataStorage.hasSelectedPostions(player)) {
				int secMinX = DataStorage.getSelectedPosFromPlayer(player)[0];
				int secMinY = DataStorage.getSelectedPosFromPlayer(player)[1];
				int secMinZ = DataStorage.getSelectedPosFromPlayer(player)[2];
				int secMaxX = x;
				int secMaxY = y;
				int secMaxZ = z; 
				int minX = MathHelper.small(secMinX, secMaxX);
				int minY = MathHelper.small(secMinY, secMaxY);
				int minZ = MathHelper.small(secMinZ, secMaxZ);
				int maxX = MathHelper.big(secMinX, secMaxX);
				int maxY = MathHelper.big(secMinY, secMaxY);
				int maxZ = MathHelper.big(secMinZ, secMaxZ);
				int blocks = 0;
				for(int xCount = minX; xCount <= maxX; ++xCount) {
					for(int yCount = minY; yCount <= maxY; ++yCount) {
						for(int zCount = minZ; zCount <= maxZ; ++zCount) {
							++blocks;
						}
					}
				}
				message += EnumChatFormatting.GREEN + " " + blocks + " block(s) selected.";
			}
			else {
				message += EnumChatFormatting.RED + " 0 block(s) selected.";
			}
			
			player.addChatMessage(message);
    	}
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
