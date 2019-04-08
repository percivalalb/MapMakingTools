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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class Pos2Command {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.pos2.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/pos2").requires((requirement) -> {
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
		
		data.setSecondPoint(new BlockPos(player));
		data.sendUpdateToClient(player);
			
		if(data.hasSelectedPoints()) {
			player.sendMessage(new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", data.getBlockCount()).applyTextStyle(TextFormatting.GREEN));
		}
		else {
			player.sendMessage(new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative").applyTextStyle(TextFormatting.RED));
		}
		
		return 1;
	}
}