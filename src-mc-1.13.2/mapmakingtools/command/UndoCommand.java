package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class UndoCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.undo.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/undo").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).executes((command) -> {
			return setBlock(command.getSource());
		}));
	}

	private static int setBlock(CommandSource source) throws CommandSyntaxException {
		Entity entity = source.getEntity();
		
		if(!(entity instanceof EntityPlayer))
			throw ERROR.create();
		
		EntityPlayer player = (EntityPlayer)entity;
		PlayerData data = WorldData.getPlayerData(player);

		if(!data.getActionStorage().hasSomethingToUndo())
			throw CommandUtil.HAS_NOTHING_TO_UNDO.create();
		
		int blocksChanged = data.getActionStorage().undo();
		
		if(blocksChanged > 0) {
			source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.build.undo.success", blocksChanged).applyTextStyle(TextFormatting.ITALIC), true);
			return 1;
		}
		
		return 0;
	}
}