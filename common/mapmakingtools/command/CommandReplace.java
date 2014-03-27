package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.tools.CachedBlock;
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
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 2)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block targetBlock = getBlockByText(sender, param[0]);
			Block replaceBlock = getBlockByText(sender, param[1]);
			int targetMeta = -1;
			int replaceMeta = 0;
			
			if(param.length >= 3)
				targetMeta = parseIntBounded(sender, param[2], -1, 15);
			if(param.length == 4)
				replaceMeta = parseIntBounded(sender, param[3], 0, 15);
			
			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			int blocks = 0;
			
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int y = data.getMinY(); y <= data.getMaxY(); ++y) {
					for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
						
						CachedBlock undo = new CachedBlock(world, x, y, z);
						
						boolean rightBlock = Block.blockRegistry.getNameForObject(targetBlock).equals(Block.blockRegistry.getNameForObject(undo.block));
						boolean rightMeta = targetMeta == -1 || undo.meta == targetMeta;
						
						if(rightBlock && rightMeta) { 
							world.setBlock(x, y, z, replaceBlock, replaceMeta, 2);
							world.setBlockMetadataWithNotify(x, y, z, replaceMeta, 2);
							list.add(undo);
							++blocks;
						}
					}
				}
			}

			data.getActionStorage().addUndo(list);

			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.replace.complete", "" + blocks, param[0]);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1 || par2ArrayOfStr.length == 2 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Block.blockRegistry.getKeys()) : par2ArrayOfStr.length == 3 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15") : par2ArrayOfStr.length == 4 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15") : null;
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
