package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldAction;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
public class CommandOutline extends CommandBase {

	@Override
	public String getName() {
		return "/outline";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.outline.usage";
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
		
		if(args.length < 1)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			Block block = getBlockByText(sender, args[0]);
			int meta = 0;
			
			if(args.length == 2)
				meta = parseInt(args[1]);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			IBlockState state = block.getStateFromMeta(meta);
			
			for(BlockPos pos : positions) {
				boolean flag1 = pos.getX() == data.getMinX();
				boolean flag2 = pos.getX() == data.getMaxX();
				boolean flag3 = pos.getZ() == data.getMinZ();
				boolean flag4 = pos.getZ() == data.getMaxZ();
				boolean flag5 = pos.getY() == data.getMinY();
				boolean flag6 = pos.getY() == data.getMaxY();
				boolean[] flags = new boolean[] {flag1, flag2, flag3, flag4, flag5, flag6};
				int count = 0;
			    for (boolean var : flags) count += (var ? 1 : 0);
				if(count > 1) {
					list.add(BlockCache.createCache(player, world, pos));
					WorldAction.setBlock(world, pos, state, false);
					blocks += 1;
				}
			}

			data.getActionStorage().addUndo(list);

			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.outline.complete", "" + blocks, args[0]);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
			
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys()) : Collections.<String>emptyList();
	}
}
