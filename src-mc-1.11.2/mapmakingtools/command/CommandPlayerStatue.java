package mapmakingtools.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jline.internal.Nullable;
import mapmakingtools.thread.PlayerStauteThread;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandPlayerStatue extends CommandBase {
	
	@Override
	public String getName() {
		return "/playerstatue";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.playerstatue.usage";
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
		
		int[] size = data.getSelectionSize();
		
		if(size[0] % 16 != 0 || size[1] % 33 != 0 || size[2] % 16 != 0)
			throw new CommandException("mapmakingtools.commands.build.playerstatue.wrongsize", new Object[0]);
		
		if(args.length < 2)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			String target = args[0];
			String facing = args[1].toLowerCase();
			boolean hat = true;
			if(args.length >= 3)
				hat = this.parseBoolean(args[2]);
			int multiplyier = size[0] / 16;
			
			if(!(this.getDirections().contains(facing)))
				throw new CommandException("mapmakingtools.commands.build.playerstatue.invaliddirection");
			
			Thread thread = new Thread(new PlayerStauteThread(data, player, target, facing, hat, multiplyier));
			thread.start();
			
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 2 ? getListOfStringsMatchingLastWord(args, getDirections()) : args.length == 3 ? getListOfStringsMatchingLastWord(args, getTrueFalse()) : Collections.<String>emptyList();
	}
	
	public static List<String> getDirections() {
		return Arrays.asList("north", "east", "south", "west");
	}
	
	public static List<String> getTrueFalse() {
		return Arrays.asList("false", "true");
	}
}
