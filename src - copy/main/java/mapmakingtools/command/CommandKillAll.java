package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.manager.ForceKillManager;
import mapmakingtools.network.packet.PacketSkullModify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 **/
public class CommandKillAll extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/killentities";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "mapmakingtools.commands.killentities.usage";
    }

    public void processCommand(ICommandSender sender, String[] param) {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		WorldServer worldServer = (WorldServer)player.worldObj;
    		if(param.length == 1) {
    			if(ForceKillManager.isRealName(param[0])) {
    				for(int i = 0; i < worldServer.loadedEntityList.size(); ++i) {
    					Entity listEntity = (Entity)worldServer.loadedEntityList.get(i);
    					ForceKillManager.killGiven(param[0], listEntity, player);
    				}
    			}
    			else {
    				throw new WrongUsageException("commands.killentities.usage", new Object[0]);
    			}
    		}
    		else {
    			throw new WrongUsageException("commands.killentities.usage", new Object[0]);
    	    }
    	}
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] param) {
    	switch(param.length) {
		case 1: 
			return getListOfStringsMatchingLastWord(param, ForceKillManager.getNameList());
    	}
    	return null;
	}

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
