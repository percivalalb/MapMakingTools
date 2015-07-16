package mapmakingtools.command;

import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandRedo extends CommandBase {

	@Override
	public String getName() {
		return "/redo";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.redo.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToRedo())
			throw new CommandException("mapmakingtools.commands.build.hasnotingtoredo", new Object[0]);
		
		int blocksChanged = data.getActionStorage().redo();
		
		if(blocksChanged > 0) {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.redo.complete", "" + blocksChanged);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
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
