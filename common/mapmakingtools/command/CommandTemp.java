package mapmakingtools.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class CommandTemp extends CommandBase {
	
	@Override
	public String getCommandName() {
		return "temp";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
	    return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.command.temp.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		EntityPlayer player = (EntityPlayer)sender;
		player.heal(-1F);
	}

	@Override
	public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return par2 == 0;
    }

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}
}
