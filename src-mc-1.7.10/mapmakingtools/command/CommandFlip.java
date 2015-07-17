package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mapmakingtools.api.enums.MovementType;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.BlockPos;
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
		return "mapmakingtools.commands.build.flip.usage";
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
			MovementType movementType = null;
			String flipMode = param[0];
			
			for(String str : getModeNames())
	    		if(str.equalsIgnoreCase(flipMode))
	    			movementType = MovementType.getRotation(str);

			if(movementType == null)
				throw new CommandException("mapmakingtools.commands.build.flipmodeerror", new Object[] {flipMode});
				
			
			data.getActionStorage().setFlipping(movementType);

			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
			}

			int blocksChanged = data.getActionStorage().flip(list);
			
			if(blocksChanged > 0) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.flip.complete", "" + blocksChanged);
				chatComponent.getChatStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param) {
        return param.length == 1 ? getListOfStringsFromIterableMatchingLastWord(param, getModeNames()) : null;
    }
	
	public static List<String> getModeNames() {
		return Arrays.asList("x", "y", "z");
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
