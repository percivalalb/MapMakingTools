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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class KillAllCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.copy.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("killentities").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).executes((command) -> {
			return setBlock(command.getSource());
		}));
	}

	private static int setBlock(CommandSource source) throws CommandSyntaxException {
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
		
		for(BlockPos pos : positions) {
			list.add(BlockCache.createCache(player, world, pos));
		}

		int blocksChanged = data.getActionStorage().addCopy(list);
			
		if(blocksChanged > 0) {
			source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.copy.success", blocksChanged).applyTextStyle(TextFormatting.ITALIC), true);
			return 1;
		}
		
		return 0;
	}
}