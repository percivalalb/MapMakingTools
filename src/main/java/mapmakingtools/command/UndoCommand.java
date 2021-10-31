package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.EditHistory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.TranslatableComponent;

import static net.minecraft.commands.Commands.literal;

public class UndoCommand {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("/undo").requires(s -> s.hasPermission(2)).executes(c -> doCommand(c)));
    }

    public static int doCommand(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        EditHistory editHistory = DimensionData.get(player.getCommandSenderWorld()).getEditHistoryManager().get(player);

        ICachedArea area;
        if ((area = editHistory.undo(player.getCommandSenderWorld())) == null) {
            source.sendFailure(new TranslatableComponent("command.mapmakingtools.undo.none"));
            return 0;
        }

        source.sendSuccess(new TranslatableComponent("command.mapmakingtools.undo.success", area.getSize()), true);
        return 1;
    }
}
