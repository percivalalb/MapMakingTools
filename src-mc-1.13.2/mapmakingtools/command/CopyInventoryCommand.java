package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class CopyInventoryCommand {
	
	private static final SimpleCommandExceptionType ERROR = new SimpleCommandExceptionType(new TextComponentTranslation("commands.mapmakingtools.copyinventory.error"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("/copyinventory").requires((requirement) -> {
			return requirement.hasPermissionLevel(2);
		}).then(Commands.argument("player", EntityArgument.player()).executes((command) -> {
			return copyInventory(command.getSource(), EntityArgument.getPlayer(command, "player"));
		})));
	}

	private static int copyInventory(CommandSource source, EntityPlayer otherPlayer) throws CommandSyntaxException {
		WorldServer world = source.getWorld();
		Entity entity = source.getEntity();
		
		if(!(entity instanceof EntityPlayer))
			throw ERROR.create();
		
		EntityPlayer player = (EntityPlayer)entity;

		player.inventory.copyInventory(otherPlayer.inventory);
		
		source.sendFeedback(new TextComponentTranslation("commands.mapmakingtools.copyinventory.success").applyTextStyle(TextFormatting.ITALIC), true);
		return 1;
	}
}