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
public class CommandShrink extends CommandBase {

	@Override
	public String getName() {
		return "/shrink";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.shrink.usage";
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
			BlockPos lowest = firstPoint;
			BlockPos highest = secondPoint;
			if(firstPoint.getX() > secondPoint.getX()) {
				lowest = secondPoint;
				highest = firstPoint;
			}
			
			if(amount < 0)
				data.setPoints(lowest.south(amount), highest);
			else if(amount > 0) 
				data.setPoints(highest.south(amount), lowest);
		}
		else if("y".equals(direction)) {
			BlockPos lowest = firstPoint;
			BlockPos highest = secondPoint;
			if(firstPoint.getY() > secondPoint.getY()) {
				lowest = secondPoint;
				highest = firstPoint;
			}
			
			if(amount < 0)
				data.setPoints(lowest.down(amount), highest);
			else if(amount > 0) 
				data.setPoints(highest.down(amount), lowest);
		}
		else if("z".equals(direction)) {
			BlockPos lowest = firstPoint;
			BlockPos highest = secondPoint;
			if(firstPoint.getZ() > secondPoint.getZ()) {
				lowest = secondPoint;
				highest = firstPoint;
			}
			
			if(amount < 0)
				data.setPoints(lowest.west(amount), highest);
			else if(amount > 0) 
				data.setPoints(highest.west(amount), lowest);
		}
		
		data.sendUpdateToClient();
		
		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.shrink.complete", direction, amount);
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
}
