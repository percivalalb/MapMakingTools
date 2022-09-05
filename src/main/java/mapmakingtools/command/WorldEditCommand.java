package mapmakingtools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.handler.Config;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.EditHistory;
import mapmakingtools.worldeditor.SelectionManager;
import mapmakingtools.worldeditor.action.FloorAction;
import mapmakingtools.worldeditor.action.MazeAction;
import mapmakingtools.worldeditor.action.RoofAction;
import mapmakingtools.worldeditor.action.SetAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import static net.minecraft.commands.Commands.literal;

public class WorldEditCommand {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext buildCtx) {
        MapMakingTools.LOGGER.info("Registering quick build commands.");
        registerSimple(dispatcher, buildCtx, "set", new SetAction());
        registerSimple(dispatcher, buildCtx, "roof", new RoofAction());
        registerSimple(dispatcher, buildCtx, "floor", new FloorAction());

        registerSimple(dispatcher, buildCtx, "maze", new MazeAction());
    }

    public static void registerSimple(final CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildCtx, String command, Action action) {
        dispatcher.register(literal(Config.SERVER.generateQuickBuildCommand(command))
                .requires(s -> s.hasPermission(2))
                .then(Commands.argument("block", BlockStateArgument.block(buildCtx))
                        .executes(c -> doCommand(c, action))));
    }

    public static int doCommand(final CommandContext<CommandSourceStack> ctx, final Action action) throws CommandSyntaxException {
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

        BlockInput input = BlockStateArgument.getBlock(ctx, "block");
        CachedBlock cache = new CachedBlock(input.getState(), null);

        ICachedArea cachedArea = action.doAction(player, selection, cache);
        EditHistory editHistory = DimensionData.get(world).getEditHistoryManager().get(player);
        editHistory.add(cachedArea);

        source.sendSuccess(Component.translatable("command.mapmakingtools.set.success", cachedArea.getSize()), true);
        return 1;
    }
}
