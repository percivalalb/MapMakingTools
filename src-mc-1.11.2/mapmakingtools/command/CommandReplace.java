package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author ProPercivalalb
 */
public class CommandReplace extends CommandBase {

	@Override
	public String getCommandName() {
		return "/replace";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.replace.usage";
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
		
		if(param.length < 4)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block targetBlock = getBlockByText(sender, param[0]);
			Block replaceBlock = getBlockByText(sender, param[2]);
			int targetMeta = parseInt(param[1], -1, 15);
			int replaceMeta = parseInt(param[3], 0, 15);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				BlockCache bse = BlockCache.createCache(player, world, pos);
				boolean rightBlock = GameRegistry.findUniqueIdentifierFor(targetBlock).equals(bse.blockIdentifier);
				boolean rightMeta = targetMeta == -1 || bse.meta == targetMeta;
						
				if(rightBlock && rightMeta) { 
					list.add(bse);
					world.setBlockState(pos, replaceBlock.getStateFromMeta(replaceMeta), 2);
					blocks += 1;
				}
			}

			data.getActionStorage().addUndo(list);

			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.replace.complete", "" + blocks, param[0]);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return par2ArrayOfStr.length == 1 || par2ArrayOfStr.length == 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, Block.blockRegistry.getKeys()) : par2ArrayOfStr.length == 3 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15") : par2ArrayOfStr.length == 4 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15") : null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
