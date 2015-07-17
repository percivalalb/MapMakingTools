package mapmakingtools.command;

import java.util.List;

import mapmakingtools.api.manager.ForceKillManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

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

    public void processCommand(ICommandSender sender, String[] param) throws CommandException {
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
    				throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
    			}
    		}
    		else {
    			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
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
