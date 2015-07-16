package mapmakingtools.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandCopyInventory extends CommandBase {

	@Override
	public String getName() {
		return "/copyinventory";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.copyinventory.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		
		EntityPlayer copyPlayer = this.getPlayer(sender, param[0]);
		
		if(copyPlayer == null || player.equals(copyPlayer))
			throw new CommandException("mapmakingtools.commands.copyinventory.playererror", new Object[0]);
	
		player.inventory.copyInventory(copyPlayer.inventory);
		
		ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.copyinventory.complete");
		chatComponent.getChatStyle().setItalic(true);
		player.addChatMessage(chatComponent);
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return index == 0;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
