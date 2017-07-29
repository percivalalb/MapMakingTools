package mapmakingtools.command;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.copyinventory.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		
		if(args.length < 1)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		
		EntityPlayer copyPlayer = this.getPlayer(server, sender, args[0]);
		
		if(copyPlayer == null || player.equals(copyPlayer))
			throw new CommandException("mapmakingtools.commands.copyinventory.playererror", new Object[0]);
	
		player.inventory.copyInventory(copyPlayer.inventory);
		
		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.copyinventory.complete");
		chatComponent.getStyle().setItalic(true);
		player.sendMessage(chatComponent);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.<String>emptyList();
    }
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return index == 0;
    }
}
