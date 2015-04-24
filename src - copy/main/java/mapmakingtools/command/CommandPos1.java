package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
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
public class CommandPos1 extends CommandBase {

	@Override
	public String getCommandName() {
		return "/pos1";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.pos1.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);

		int posX = MathHelper.floor_double(data.getPlayer().posX);
		int posY = MathHelper.floor_double(data.getPlayer().posY);
		int posZ = MathHelper.floor_double(data.getPlayer().posZ);
		data.setFirstPoint(posX, posY, posZ);
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
	public List addTabCompletionOptions(ICommandSender sender, String[] param) {
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
