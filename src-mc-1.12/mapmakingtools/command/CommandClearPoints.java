package mapmakingtools.command;

import java.util.Collections;
import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.clearpoints.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = args.length == 0 ? this.getCommandSenderAsPlayer(sender) : this.getPlayer(server, sender, args[0]);
		
		PlayerData data = WorldData.getPlayerData(player);
		data.setFirstPoint(null);
		data.setSecondPoint(null);
		data.sendUpdateToClient();
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.<String>emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
