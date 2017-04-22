package mapmakingtools.command;

import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandUndo extends CommandBase {

	@Override
	public String getName() {
		return "/undo";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.undo.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToUndo())
			throw new CommandException("mapmakingtools.commands.build.hasnotingtoundo", new Object[0]);
		
		int blocksChanged = data.getActionStorage().undo();
		
		if(blocksChanged > 0) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.undo.complete", "" + blocksChanged);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
