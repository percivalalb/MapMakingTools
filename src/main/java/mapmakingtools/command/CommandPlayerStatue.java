package mapmakingtools.command;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import cpw.mods.fml.common.FMLLog;
import mapmakingtools.MapMakingTools;
import mapmakingtools.thread.PlayerStauteThread;
import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.datareader.BlockColourList;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandPlayerStatue extends CommandBase {
	
	@Override
	public String getCommandName() {
		return "/playerstatue";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.playerstatue.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		int[] size = data.getSelectionSize();
		
		if(size[0] % 16 != 0 || size[1] % 33 != 0 || size[2] % 16 != 0)
			throw new CommandException("mapmakingtools.commands.build.playerstatue.wrongsize", new Object[0]);
		
		if(param.length < 2)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			String target = param[0];
			String facing = param[1].toLowerCase();
			boolean hat = true;
			if(param.length >= 3)
				hat = this.parseBoolean(sender, param[2]);
			int multiplyier = size[0] / 16;
			
			if(!(this.getDirections().contains(facing)))
				throw new CommandException("mapmakingtools.commands.build.playerstatue.invaliddirection");
			
			Thread thread = new Thread(new PlayerStauteThread(data, player, target, facing, hat, multiplyier));
			thread.start();
			
		}
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param) {
        return param.length == 2 ? getListOfStringsFromIterableMatchingLastWord(param, getDirections()) : param.length == 3 ? getListOfStringsFromIterableMatchingLastWord(param, getTrueFalse()) : null;
    }
	
	public static List<String> getDirections() {
		return Arrays.asList("north", "east", "south", "west");
	}
	
	public static List<String> getTrueFalse() {
		return Arrays.asList("false", "true");
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
