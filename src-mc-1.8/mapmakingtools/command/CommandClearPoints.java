package mapmakingtools.command;

import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

/**
 * @author ProPercivalalb
 */
public class CommandClearPoints extends CommandBase {

	@Override
	public String getName() {
		return "/clearpoints";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.clearpoints.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayerMP player = param.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, param[0]);
		
		PlayerData data = WorldData.getPlayerData(player);
		data.setFirstPoint(null);
		data.setSecondPoint(null);
		data.sendUpdateToClient();
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers()) : null;
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
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
