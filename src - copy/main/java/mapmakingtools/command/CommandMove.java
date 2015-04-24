package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;

import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.SelectedPoint;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * @author ProPercivalalb
 */
public class CommandMove extends CommandBase {

	@Override
	public String getCommandName() {
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
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		SelectedPoint firstPoint = data.getFirstPoint();
		SelectedPoint secondPoint = data.getSecondPoint();
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 2)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		
		String direction = param[0].toLowerCase();
		int amount = this.parseInt(sender, param[1]);
		
		if("x".equals(direction)) {
			firstPoint.moveX(amount);
			secondPoint.moveX(amount);
		}
		else if("y".equals(direction)) {
			firstPoint.moveY(amount);
			secondPoint.moveY(amount);
		}
		else if("z".equals(direction)) {
			firstPoint.moveZ(amount);
			secondPoint.moveZ(amount);
		}
		
		data.sendUpdateToClient();
		
		ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.move.complete", direction, amount);
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
