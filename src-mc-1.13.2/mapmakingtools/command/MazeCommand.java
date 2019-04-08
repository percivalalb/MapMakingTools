package mapmakingtools.command;

import java.util.ArrayList;
import java.util.HashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldAction;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class MazeCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.maze.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/maze").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).then(Commands.argument("block", BlockStateArgument.blockState()).executes((command) -> {
			return setBlock(command.getSource(), BlockStateArgument.getBlockState(command, "block"));
		})));
	}

	private static int setBlock(CommandSource source, BlockStateInput block) throws CommandSyntaxException {
		WorldServer world = source.getWorld();
		Entity entity = source.getEntity();
		
		if(!(entity instanceof EntityPlayer))
			throw ERROR.create();
		
		EntityPlayer player = (EntityPlayer)entity;
		PlayerData data = WorldData.getPlayerData(player);
      
		if(!data.hasSelectedPoints())
			throw CommandUtil.NO_POINTS_SELECTED.create();
		
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
			else {
				block.place(world, pos, 2);
			}
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
			for(int y = data.getMinY(); y <= data.getMaxY(); y++) {
				WorldAction.setBlockToAir(world, dir.add(0, y, 0).add(intersectionPos), false);
			}
			
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

		source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.maze.success", block).applyTextStyle(TextFormatting.ITALIC), true);
		data.getActionStorage().addUndo(list);
		return 1;
	}
	
	public static BlockPos multiply(BlockPos pos, int multiply) {
		return pos.add(pos.getX() * (multiply - 1), pos.getY() * (multiply - 1), pos.getZ() * (multiply - 1));
	}
}