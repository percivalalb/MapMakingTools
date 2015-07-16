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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
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
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.move.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
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
		int amount = this.parseInt(param[1]);
		
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
		
		ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.move.complete", direction, amount);
		chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
		player.addChatMessage(chatComponent);
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param, BlockPos pos) {
        return param.length == 1 ? func_175762_a(param, getDirections()) : null;
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
