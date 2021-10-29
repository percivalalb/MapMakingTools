package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.command.argument.BiomeArgument;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import static net.minecraft.command.Commands.literal;

public class SetBiomeCommand {

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("/setbioem")
                .requires(s -> s.hasPermission(2))
                .then(Commands.argument("biome", BiomeArgument.biome())
                        .executes(c -> doCommand(c))));
    }

    public static int doCommand(final CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        CommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayerOrException();
        World world = player.getCommandSenderWorld();

        DimensionData dimData = DimensionData.get(player.getCommandSenderWorld());
        SelectionManager selectionManager = dimData.getSelectionManager();

        ISelection selection = selectionManager.get(player);

        if (!selection.isSet()) {
            source.sendFailure(new TranslationTextComponent("world_editor.mapmakingtools.selection.none"));
            return 0;
        }

        Biome input = BiomeArgument.getBiome(ctx, "biome");

        Iterable<BlockPos> positions = BlockPos.betweenClosed(selection.getPrimaryPoint(), selection.getSecondaryPoint());

        for (BlockPos pos : positions) {

        }

        source.sendSuccess(new TranslationTextComponent("command.mapmakingtools.set.success"), true);
        return 1;
    }
}
