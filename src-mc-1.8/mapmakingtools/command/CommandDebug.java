package mapmakingtools.command;

import java.util.List;

import mapmakingtools.MapMakingTools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

/**
 * @author ProPercivalalb
 */
public class CommandDebug extends CommandBase {

	@Override
	public String getName() {
		return "/debug";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.debug.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		
		if(param.length >= 1) {
			if(param[0].toLowerCase().equals("reloadrotations")) {
				MapMakingTools.proxy.registerRotation();
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, "reloadrotations") : null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
