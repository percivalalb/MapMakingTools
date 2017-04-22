package mapmakingtools.command;

import java.util.Collections;
import java.util.List;

import jline.internal.Nullable;
import mapmakingtools.api.manager.ForceKillManager;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

/**
 * @author ProPercivalalb
 **/
public class CommandKillAll extends CommandBase {
	
	@Override
    public String getName() {
        return "/killentities";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "mapmakingtools.commands.killentities.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		WorldServer worldServer = (WorldServer)player.world;
    		if(args.length == 1) {
    			if(ForceKillManager.isRealName(args[0])) {
    				for(int i = 0; i < worldServer.loadedEntityList.size(); ++i) {
    					Entity listEntity = (Entity)worldServer.loadedEntityList.get(i);
    					ForceKillManager.killGiven(args[0], listEntity, player);
    				}
    			}
    			else {
    				throw new WrongUsageException(this.getUsage(sender), new Object[0]);
    			}
    		}
    		else {
    			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
    	    }
    	}
    }

    @Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, ForceKillManager.getNameList()) : Collections.<String>emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
