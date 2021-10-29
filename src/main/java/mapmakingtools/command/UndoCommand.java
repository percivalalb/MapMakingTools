package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.EditHistory;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.literal;

public class UndoCommand {

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("/undo").requires(s -> s.hasPermission(2)).executes(c -> doCommand(c)));
    }

    public static int doCommand(final CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        CommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayerOrException();
        EditHistory editHistory = DimensionData.get(player.getCommandSenderWorld()).getEditHistoryManager().get(player);

        ICachedArea area;
        if ((area = editHistory.undo(player.getCommandSenderWorld())) == null) {
            source.sendFailure(new TranslationTextComponent("command.mapmakingtools.undo.none"));
            return 0;
        }

        source.sendSuccess(new TranslationTextComponent("command.mapmakingtools.undo.success", area.getSize()), true);
        return 1;
    }
}
