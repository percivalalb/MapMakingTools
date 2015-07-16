package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.tools.BlockCache;
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
public class CommandCopy extends CommandBase {

	@Override
	public String getName() {
		return "/copy";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.copy.usage";
	}

	@Override
	public void execute(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);

		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			
		Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
		
		for(BlockPos pos : positions) {
			list.add(BlockCache.createCache(player, world, pos));
		}

		int blocksChanged = data.getActionStorage().addCopy(list);
			
		if(blocksChanged > 0) {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.copy.complete", "" + blocksChanged);
			chatComponent.getChatStyle().setItalic(true);
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
