package mapmakingtools.thread;

import java.util.ArrayList;
import java.util.HashMap;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class MazeThread extends Thread {
	
	public World world;
	public PlayerData data;
	public IBlockState state;

	public MazeThread(World world, PlayerData data, IBlockState state) {
		this.world = world;
		this.data = data;
		this.state = state;
	}
	
	public BlockPos multiply(BlockPos pos, int multiply) {
		return pos.add(pos.getX() * (multiply - 1), pos.getY() * (multiply - 1), pos.getZ() * (multiply - 1));
	}
	
	@Override
	public void run() {

		//synchronized(this.world) {
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
		//}
		
/**
		ArrayList<BlockCache> list = new ArrayList<BlockCache>();

		Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
		
		HashMap<Long, Integer> groups = new HashMap<Long, Integer>();
		int group = 0;
		for(BlockPos pos : positions) {
			list.add(BlockCache.createCache(player, world, pos));
			
			if ((pos.getX() - data.getMinX()) % 2 == 1 && (pos.getZ() - data.getMinZ()) % 2 == 1 && pos.getX() != data.getMaxX() && pos.getZ() != data.getMaxZ()) {
				world.setBlockToAir(pos);
				groups.put(new BlockPos(pos.getX(), 0, pos.getZ()).toLong(), group);
				group += 1;
			}
			else
				world.setBlockState(pos, block.getStateFromMeta(meta));
		}
		
		while(true) {
			long intersection = (long)groups.keySet().toArray()[world.rand.nextInt(groups.size())];
			BlockPos intersectionPos = BlockPos.fromLong(intersection);

			BlockPos dir = new BlockPos[] {new BlockPos(0, 0, 1), 
										   new BlockPos(1, 0, 0), 
										   new BlockPos(0, 0, -1), 
										   new BlockPos(-1, 0, 0)} [world.rand.nextInt(4)];
					
			if(world.isAirBlock(dir.add(0, data.getMinY(), 0).add(intersectionPos)))
				continue;

			
			long nextIntersection = dir.multiply(2).add(intersectionPos).toLong();
			
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
				world.setBlockToAir(dir.add(0, y, 0).add(intersectionPos));
			
			//Checks if all groups are the same - all pathways are connected
			boolean done = true;
			for(Long spot : groups.keySet()) {
				if(groups.get(spot) != thisid) {
					done = false;
;						break;
				}
			}
			
			if(done)
				break;
		}
		
		data.getActionStorage().addUndo(list);

		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.maze.complete", param[0]);
		chatComponent.getStyle().setItalic(true);
		player.sendMessage(chatComponent);**/
	}
}
