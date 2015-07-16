package mapmakingtools.command;

import java.util.Arrays;
import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandExpand extends CommandBase {

	@Override
	public String getCommandName() {
		return "/expand";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.expand.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		BlockPos firstPoint = data.getFirstPoint();
		BlockPos secondPoint = data.getSecondPoint();
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 2)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		
		String direction = param[0].toLowerCase();
		int amount = this.parseInt(sender, param[1]);
		
		if("x".equals(direction)) {
			BlockPos lowest = firstPoint;
			BlockPos highest = secondPoint;
			if(firstPoint.getX() > secondPoint.getX())
				lowest = secondPoint;
				highest = firstPoint;
			
			if(amount < 0)
				data.setPoints(highest.add(amount, 0, 0), lowest);
			else if(amount > 0) 
				data.setPoints(lowest.add(-amount, 0, 0), highest);
		}
		else if("y".equals(direction)) {
			BlockPos lowest = firstPoint;
			BlockPos highest = secondPoint;
			if(firstPoint.getY() > secondPoint.getY())
				lowest = secondPoint;
				highest = firstPoint;
			
			if(amount < 0)
				data.setPoints(lowest.add(0, amount, 0), highest);
			else if(amount > 0) 
				data.setPoints(highest.add(0, amount, 0), lowest);
		}
		else if("z".equals(direction)) {
			BlockPos lowest = firstPoint;
			BlockPos highest = secondPoint;
			if(firstPoint.getZ() > secondPoint.getZ())
				lowest = secondPoint;
				highest = firstPoint;
			
			if(amount < 0)
				data.setPoints(lowest.add(0, 0, amount), highest);
			else if(amount > 0) 
				data.setPoints(highest.add(0, 0, amount), lowest);
		}
		
		data.sendUpdateToClient();
		
		ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.expand.complete", direction, amount);
		chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
		player.addChatMessage(chatComponent);
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param) {
        return param.length == 1 ? getListOfStringsFromIterableMatchingLastWord(param, getDirections()) : null;
    }
	
	public static List<String> getDirections() {
		return Arrays.asList("x", "y", "z");
	}
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}
}
