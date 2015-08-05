package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mapmakingtools.thread.MazeThread;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

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
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.maze.usage";
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
				Long[] keys = groups.keySet().toArray(new Long[groups.size()]);
				long intersection = keys[world.rand.nextInt(groups.size())];
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
							break;
					}
				}
				
				if(done)
					break;
			}
			
			data.getActionStorage().addUndo(list);

			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.maze.complete", param[0]);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
			//MazeThread mazeThread = new MazeThread(world, data);
			//new Thread(mazeThread).start();
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
