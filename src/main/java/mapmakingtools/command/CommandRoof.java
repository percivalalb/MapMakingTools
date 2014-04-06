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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandRoof extends CommandBase {

	@Override
	public String getCommandName() {
		return "/roof";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.roof.usage";
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
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block block = getBlockByText(sender, param[0]);
			int meta = 0;
			
			if(param.length == 2)
				meta = parseInt(sender, param[1]);
			
			String displayName = block != Blocks.air ? new ItemStack(block, 1, meta).getDisplayName() : StatCollector.translateToLocal("mapmakingtools.commands.build.air");
			
			int maxY = data.getMaxY();
			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			int blocks = 0;
			
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
					CachedBlock undo = new CachedBlock(world, x, maxY, z);
					world.setBlock(x, maxY, z, block, meta, 2);
					world.setBlockMetadataWithNotify(x, maxY, z, meta, 2);
					list.add(undo);
					++blocks;
				}
			}

			data.getActionStorage().addUndo(list);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.roof.complete", "" + blocks, displayName);
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