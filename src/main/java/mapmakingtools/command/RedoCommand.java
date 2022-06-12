package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.EditHistory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class RedoCommand {

    private static final DynamicCommandExceptionType SPAWN_EXCEPTION = new DynamicCommandExceptionType((arg) -> {
        return Component.translatable("command.mapmakingtools.undo.nothing", arg);
    });

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("/redo").requires(s -> s.hasPermission(2)).executes(c -> doCommand(c)));
    }

    public static int doCommand(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        EditHistory editHistory = DimensionData.get(player.getCommandSenderWorld()).getEditHistoryManager().get(player);

        ICachedArea area;
        if ((area = editHistory.redo(player.getCommandSenderWorld())) == null) {
            source.sendFailure(Component.translatable("command.mapmakingtools.redo.none"));
            return 0;
        }

        source.sendSuccess(Component.translatable("command.mapmakingtools.redo.success", area.getSize()), true);
        return 1;
    }
}
