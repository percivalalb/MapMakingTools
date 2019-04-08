package mapmakingtools.command;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class ReplaceCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.replace.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/replace").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).then(Commands.argument("block", BlockStateArgument.blockState()).then(Commands.argument("replace", BlockStateArgument.blockState()).executes((command) -> {
			return setBlock(command.getSource(), BlockStateArgument.getBlockState(command, "block"), BlockStateArgument.getBlockState(command, "replace"));
		}))));
	}

	private static int setBlock(CommandSource source, BlockStateInput block, BlockStateInput replace) throws CommandSyntaxException {
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
			if(replace.test(new BlockWorldState(world, pos, true)) && block.place(world, pos, 2)) {
				blocks += 1;
			}
		}
  	  	
		source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.replace.success", blocks), true);
		data.getActionStorage().addUndo(list);
		return 1;
	}
}