package mapmakingtools.command;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandPos1 extends CommandBase {

	@Override
	public String getName() {
		return "/pos1";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.pos1.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);

		data.setFirstPoint(new BlockPos(player));
		data.sendUpdateToClient();
			
		if(data.hasSelectedPoints()) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + data.getBlockCount());
			chatComponent.getStyle().setColor(TextFormatting.GREEN);
			player.sendMessage(chatComponent);
		}
		else {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
			chatComponent.getStyle().setColor(TextFormatting.RED);
			player.sendMessage(chatComponent);
		}
	}
}
