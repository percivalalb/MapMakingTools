package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import mapmakingtools.command.argument.RotationArgument;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Rotation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class RotateCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.build.rotate.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/rotate").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).then(Commands.argument("rotation", RotationArgument.rotation()).executes((command) -> {
			return setBlock(command.getSource(), RotationArgument.getRotation(command, "rotation"));
		})));
	}

	private static int setBlock(CommandSource source, Rotation rotation) throws CommandSyntaxException {
		WorldServer world = source.getWorld();
		Entity entity = source.getEntity();
		
		if(!(entity instanceof EntityPlayer))
			throw ERROR.create();
		
		EntityPlayer player = (EntityPlayer)entity;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToPaste())
			throw CommandUtil.NOTHING_TO_PASTE.create();
		
		boolean didChange = data.getActionStorage().setRotation(rotation);
		
		if(didChange) {
			player.sendMessage(new TextComponentTranslation("commands.mapmakingtools.build.rotate.success").applyTextStyle(TextFormatting.ITALIC));
			return 1;
		}
		
		return 0;
	}
}