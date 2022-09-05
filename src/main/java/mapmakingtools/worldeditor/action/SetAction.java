package mapmakingtools.worldeditor.action;

import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.worldeditor.CachedCuboidArea;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class SetAction implements Action {

    @Override
    public ICachedArea doAction(Player player, ISelection selection, CachedBlock input) {
        ICachedArea cachedArea = CachedCuboidArea.from(player.getCommandSenderWorld(), selection);

        Iterable<BlockPos> positions = BlockPos.betweenClosed(selection.getPrimaryPoint(), selection.getSecondaryPoint());

        for (BlockPos pos : positions) {
            input.place(player.getCommandSenderWorld(), pos);
        }

        return cachedArea;
    }
}
