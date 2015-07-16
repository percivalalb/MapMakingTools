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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandPos2 extends CommandBase {

	@Override
	public String getName() {
		return "/pos2";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.pos2.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		data.setSecondPoint(new BlockPos(player));
		data.sendUpdateToClient();
		
		if(data.hasSelectedPoints()) {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + data.getBlockCount());
			chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
			player.addChatMessage(chatComponent);
		}
		else {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
			chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
			player.addChatMessage(chatComponent);
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param, BlockPos pos) {
        return null;
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
