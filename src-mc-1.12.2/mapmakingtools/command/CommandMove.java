package mapmakingtools.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandMove extends CommandBase {

	@Override
	public String getName() {
		return "/move";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.move.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		BlockPos firstPoint = data.getFirstPoint();
		BlockPos secondPoint = data.getSecondPoint();
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(args.length < 2)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		
		String direction = args[0].toLowerCase();
		int amount = this.parseInt(args[1]);
		
		if("x".equals(direction)) {
			data.setFirstPoint(firstPoint.north(amount));
			data.setSecondPoint(secondPoint.north(amount));
		}
		else if("y".equals(direction)) {
			data.setFirstPoint(firstPoint.up(amount));
			data.setSecondPoint(secondPoint.up(amount));
		}
		else if("z".equals(direction)) {
			data.setFirstPoint(firstPoint.east(amount));
			data.setSecondPoint(secondPoint.east(amount));
		}
		
		data.sendUpdateToClient();
		
		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.move.complete", direction, amount);
		chatComponent.getStyle().setColor(TextFormatting.GREEN);
		player.sendMessage(chatComponent);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getDirections()) : Collections.<String>emptyList();
	}
	
	public static List<String> getDirections() {
		return Arrays.asList("x", "y", "z");
	}
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
}
