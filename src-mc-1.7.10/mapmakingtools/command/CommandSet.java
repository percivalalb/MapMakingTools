package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
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
public class CommandSet extends CommandBase {

	@Override
	public String getCommandName() {
		return "/set";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.set.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block block = getBlockByText(sender, param[0]);
			int meta = 0;
			
			if(param.length == 2)
				meta = parseInt(sender, param[1]);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
				data.getPlayerWorld().setBlock(pos.getX(), pos.getY(), pos.getZ(), block, meta, 2);
				blocks += 1;
			}

			data.getActionStorage().addUndo(list);

			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.set.complete", "" + blocks, param[0]);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Block.blockRegistry.getKeys()) : null;
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
