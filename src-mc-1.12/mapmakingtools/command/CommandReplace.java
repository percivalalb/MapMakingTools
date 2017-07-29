package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandReplace extends CommandBase {

	@Override
	public String getName() {
		return "/replace";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.replace.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(args.length < 4)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			Block targetBlock = getBlockByText(sender, args[0]);
			Block replaceBlock = getBlockByText(sender, args[2]);
			int targetMeta = parseInt(args[1], -1, 15);
			int replaceMeta = parseInt(args[3], 0, 15);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				BlockCache bse = BlockCache.createCache(player, world, pos);
				boolean rightBlock = bse.getReplacedBlock().getBlock().equals(targetBlock);
				boolean rightMeta = targetMeta == -1 || bse.meta == targetMeta;
						
				if(rightBlock && rightMeta) { 
					list.add(bse);
					world.setBlockState(pos, replaceBlock.getStateFromMeta(replaceMeta), 2);
					blocks += 1;
				}
			}

			data.getActionStorage().addUndo(list);

			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.replace.complete", "" + blocks, args[0]);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
			
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 || args.length == 2 ? getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys()) : args.length == 3 ? getListOfStringsMatchingLastWord(args, "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15") : args.length == 4 ? getListOfStringsMatchingLastWord(args, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15") : Collections.<String>emptyList();
	}
}
