package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.EditHistory;
import mapmakingtools.worldeditor.SelectionManager;
import mapmakingtools.worldeditor.action.FloorAction;
import mapmakingtools.worldeditor.action.MazeAction;
import mapmakingtools.worldeditor.action.RoofAction;
import mapmakingtools.worldeditor.action.SetAction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static net.minecraft.command.Commands.literal;

public class WorldEditCommand {

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        registerSimple(dispatcher, "/set", new SetAction());
        registerSimple(dispatcher, "/roof", new RoofAction());
        registerSimple(dispatcher, "/floor", new FloorAction());

        registerSimple(dispatcher, "/maze", new MazeAction());
    }

    public static void registerSimple(final CommandDispatcher<CommandSource> dispatcher, String command, Action action) {
        dispatcher.register(literal(command)
                .requires(s -> s.hasPermissionLevel(2))
                .then(Commands.argument("block", BlockStateArgument.blockState())
                        .executes(c -> doCommand(c, action))));
    }

    public static int doCommand(final CommandContext<CommandSource> ctx, final Action action) throws CommandSyntaxException {
        CommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.asPlayer();
        World world = player.getEntityWorld();

        DimensionData dimData = DimensionData.get(player.getEntityWorld());
        SelectionManager selectionManager = dimData.getSelectionManager();

        ISelection selection = selectionManager.get(player);

        if (!selection.isSet()) {
            source.sendErrorMessage(new TranslationTextComponent("world_editor.mapmakingtools.selection.none"));
            return 0;
        }

        BlockStateInput input = BlockStateArgument.getBlockState(ctx, "block");
        CachedBlock cache = new CachedBlock(input.getState(), null);

        ICachedArea cachedArea = action.doAction(player, selection, cache);
        EditHistory editHistory = DimensionData.get(world).getEditHistoryManager().get(player);
        editHistory.add(cachedArea);

        source.sendFeedback(new TranslationTextComponent("command.mapmakingtools.set.success", cachedArea.getSize()), true);
        return 1;
    }
}
