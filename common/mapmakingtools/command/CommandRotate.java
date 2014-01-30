package mapmakingtools.command;

import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandRotate extends CommandBase {

	@Override
	public String getCommandName() {
		return "/rotate";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.rotate.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToPaste())
			throw new CommandException("mapmakingtools.commands.build.nothingtorotate", new Object[0]);
		
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			boolean didChange = data.getActionStorage().setRotation(parseInt(sender, param[0]));
			
			if(didChange) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.rotate.complete", param[0]);
				chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
				player.func_145747_a(chatComponent);
			}
			else {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.rotationnot90");
				chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
				player.func_145747_a(chatComponent);
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
