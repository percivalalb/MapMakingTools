package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jline.internal.Nullable;
import mapmakingtools.api.manager.ForceKillManager;
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
public class CommandMaze extends CommandBase {

	@Override
	public String getName() {
		return "/maze";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.maze.usage";
	}

	public BlockPos multiply(BlockPos pos, int multiply) {
		return pos.add(pos.getX() * (multiply - 1), pos.getY() * (multiply - 1), pos.getZ() * (multiply - 1));
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
			
			IBlockState state = block.getStateFromMeta(meta);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();

			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			HashMap<Long, Integer> groups = new HashMap<Long, Integer>();
			int group = 0;
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(data.getPlayer(), world, pos));
				
				if ((pos.getX() - data.getMinX()) % 2 == 1 && (pos.getZ() - data.getMinZ()) % 2 == 1 && pos.getX() != data.getMaxX() && pos.getZ() != data.getMaxZ()) {
					WorldAction.setBlockToAir(world, pos, false);
					groups.put(new BlockPos(pos.getX(), 0, pos.getZ()).toLong(), group);
					group += 1;
				}
				else
					WorldAction.setBlock(world, pos, state, false);
			}
			
			while(true) {
				Long[] keys = groups.keySet().toArray(new Long[groups.size()]);
				long intersection = keys[world.rand.nextInt(groups.size())];
				BlockPos intersectionPos = BlockPos.fromLong(intersection);

				BlockPos dir = new BlockPos[] {new BlockPos(0, 0, 1),
											   new BlockPos(1, 0, 0),
											   new BlockPos(0, 0, -1),
											   new BlockPos(-1, 0, 0)} [world.rand.nextInt(4)];
						
				if(world.isAirBlock(dir.add(0, data.getMinY(), 0).add(intersectionPos)))
					continue;
				
				long nextIntersection = multiply(dir, 2).add(intersectionPos).toLong();
				
				//Spot it wants to connect to doesn't exist
				if(!groups.containsKey(nextIntersection))
					continue;
				
				int thisid = groups.get(intersection);
				int oldid = groups.get(nextIntersection);
				
				if(oldid == thisid) //Already Connected
					continue;
				
				//Set the old group to the next one combining the groups
				for(Long spot : groups.keySet())
					if(groups.get(spot) == oldid)
						groups.put(spot, thisid);
				
				//Clear pathway
				for(int y = data.getMinY(); y <= data.getMaxY(); y++)
					WorldAction.setBlockToAir(world, dir.add(0, y, 0).add(intersectionPos), false);
				
				//Checks if all groups are the same - all pathways are connected
				boolean done = true;
				for(Long spot : groups.keySet()) {
					if(groups.get(spot) != thisid) {
						done = false;
							break;
					}
				}
				
				if(done)
					break;
			}
			
			data.getActionStorage().addUndo(list);

			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.maze.complete", Block.REGISTRY.getNameForObject(state.getBlock()));
			chatComponent.getStyle().setItalic(true);
			data.getPlayer().sendMessage(chatComponent);
			
			//System.out.println("" + data);
			//MazeThread mazeThread = new MazeThread(world, data, block.getStateFromMeta(meta));
			//new Thread(mazeThread).start();
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys()) : Collections.<String>emptyList();
	}
}
