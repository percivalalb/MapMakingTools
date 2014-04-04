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
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandPaste extends CommandBase {

	@Override
	public String getCommandName() {
		return "/paste";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.paste.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToPaste())
			throw new CommandException("mapmakingtools.commands.build.nothingtopaste", new Object[0]);

		ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			
		for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
			for(int y = data.getMinY(); y <= data.getMaxY(); ++y) {
				for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
					CachedBlock undo = new CachedBlock(world, x, y, z);
					list.add(undo);
				}
			}
		}

		int blocksChanged = data.getActionStorage().paste();
			
		if(blocksChanged > 0) {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.paste.complete", "" + blocksChanged);
			chatComponent.getChatStyle().setItalic(true);
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
