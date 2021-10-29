package mapmakingtools.worldeditor.action;

import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.worldeditor.CachedCuboidArea;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SetAction implements Action {

    @Override
    public ICachedArea doAction(PlayerEntity player, ISelection selection, CachedBlock input) {
        ICachedArea cachedArea = CachedCuboidArea.from(player.getCommandSenderWorld(), selection);

        Iterable<BlockPos> positions = BlockPos.betweenClosed(selection.getPrimaryPoint(), selection.getSecondaryPoint());

        for (BlockPos pos : positions) {
            input.place(player.getCommandSenderWorld(), pos);
        }

        return cachedArea;
    }
}
