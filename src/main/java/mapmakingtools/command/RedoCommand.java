package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.EditHistory;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import static net.minecraft.command.Commands.literal;

public class RedoCommand {

    public static final DynamicCommandExceptionType SPAWN_EXCEPTION = new DynamicCommandExceptionType((arg) -> {
        return new TranslationTextComponent("command.mapmakingtools.undo.nothing", arg);
    });

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("/redo").requires(s -> s.hasPermissionLevel(2)).executes(c -> doCommand(c)));
    }

    public static int doCommand(final CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        CommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.asPlayer();
        EditHistory editHistory = DimensionData.get(player.getEntityWorld()).getEditHistoryManager().get(player);

        ICachedArea area;
        if ((area = editHistory.redo(player.getEntityWorld())) == null) {
            source.sendErrorMessage(new TranslationTextComponent("command.mapmakingtools.redo.none"));
            return 0;
        }

        source.sendFeedback(new TranslationTextComponent("command.mapmakingtools.redo.success", area.getSize()), true);
        return 1;
    }
}
