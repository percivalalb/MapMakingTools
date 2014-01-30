package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandFlip extends CommandBase {

	@Override
	public String getCommandName() {
		return "/flip";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.rotate.usage";
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
			int flipIndex = -1;
			String flipMode = param[0];
			
			for(String str : getModeNames())
	    		if(str.equalsIgnoreCase(flipMode))
	    			flipIndex = getModeNames().indexOf(str);

			if(flipIndex == -1)
				throw new CommandException("mapmakingtools.commands.build.flipmodeerror", new Object[] {flipMode});
				
			
			data.getActionStorage().setFlipping(flipIndex);

			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int y = data.getMinY(); y <= data.getMaxY(); ++y) {
					for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
						CachedBlock undo = new CachedBlock(world, x, y, z);
						list.add(undo);
					}
				}
			}

			int blocksChanged = data.getActionStorage().flip(list);
			
			if(blocksChanged > 0) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.flip.complete", "" + blocksChanged);
				chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
				player.func_145747_a(chatComponent);
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param) {
        return param.length == 1 ? getListOfStringsFromIterableMatchingLastWord(param, getModeNames()) : null;
    }
	
	public static List<String> getModeNames() {
		return Arrays.asList("yVertically", "xHorizontal", "zHorizontal");
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
