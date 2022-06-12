package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.command.argument.BiomeArgument;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import static net.minecraft.commands.Commands.literal;

public class SetBiomeCommand {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("/setbioem")
                .requires(s -> s.hasPermission(2))
                .then(Commands.argument("biome", BiomeArgument.biome())
                        .executes(c -> doCommand(c))));
    }

    public static int doCommand(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        Level world = player.getCommandSenderWorld();

        DimensionData dimData = DimensionData.get(player.getCommandSenderWorld());
        SelectionManager selectionManager = dimData.getSelectionManager();

        ISelection selection = selectionManager.get(player);

        if (!selection.isSet()) {
            source.sendFailure(Component.translatable("world_editor.mapmakingtools.selection.none"));
            return 0;
        }

        Biome input = BiomeArgument.getBiome(ctx, "biome");

        Iterable<BlockPos> positions = BlockPos.betweenClosed(selection.getPrimaryPoint(), selection.getSecondaryPoint());

        for (BlockPos pos : positions) {

        }

        source.sendSuccess(Component.translatable("command.mapmakingtools.set.success"), true);
        return 1;
    }
}
