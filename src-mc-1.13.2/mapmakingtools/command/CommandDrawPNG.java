package mapmakingtools.command;

import java.util.Collections;
import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandDrawPNG extends CommandBase {
	
	@Override
	public String getName() {
		return "/drawpng";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.drawpng.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		
		//TODO PacketDispatcher.sendTo(new PacketRequestPNG(args[0]), player);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return Collections.<String>emptyList();
	}
}
