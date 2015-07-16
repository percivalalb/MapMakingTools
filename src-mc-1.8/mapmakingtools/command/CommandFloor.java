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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandFloor extends CommandBase {

	@Override
	public String getName() {
		return "/floor";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.floor.usage";
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
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block block = getBlockByText(sender, param[0]);
			int meta = 0;
			
			if(param.length == 2)
				meta = parseInt(param[1]);
			
			int minY = data.getMinY();
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), minY, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), minY, data.getSecondPoint().getZ()));
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
				world.setBlockState(pos, block.getStateFromMeta(meta), 2);
				blocks += 1;
			}

			data.getActionStorage().addUndo(list);

			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.floor.complete", "" + blocks, param[0]);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return par2ArrayOfStr.length == 1 ? func_175762_a(par2ArrayOfStr, Block.blockRegistry.getKeys()) : null;
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
