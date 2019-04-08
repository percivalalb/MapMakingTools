package mapmakingtools.command;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
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

public class SetCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.set.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/set").requires((requirement) -> {
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
		
  	  	Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
		
  	  	int blocks = 0;
  		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
  	  	
  	  	for(BlockPos pos : positions) {
			list.add(BlockCache.createCache(player, world, pos));
			  /**
			    * Sets a block state into this world.Flags are as follows:
			    * 1 will cause a block update.
			    * 2 will send the change to clients.
			    * 4 will prevent the block from being re-rendered.
			    * 8 will force any re-renders to run on the main thread instead
			    * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
			    * 32 will prevent neighbor reactions from spawning drops.
			    * 64 will signify the block is being moved.
			    * Flags can be OR-ed
			    */
			if(block.place(world, pos, 2)) {
				blocks += 1;
			}
		}
  	  	
		source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.set.success", blocks).applyTextStyle(TextFormatting.ITALIC), true);
		data.getActionStorage().addUndo(list);
		return 1;
	}
}