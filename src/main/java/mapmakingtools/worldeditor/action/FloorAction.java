package mapmakingtools.worldeditor.action;

import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.worldeditor.CachedCuboidArea;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class FloorAction implements Action {

    @Override
    public ICachedArea doAction(Player player, ISelection selection, CachedBlock input) {
        BlockPos.MutableBlockPos pos1 = selection.getPrimaryPoint().mutable();
        BlockPos.MutableBlockPos pos2 = selection.getSecondaryPoint().mutable();
        pos1.setY(selection.getMinY());
        pos2.setY(selection.getMinY());

        ICachedArea cachedArea = CachedCuboidArea.from(player.getCommandSenderWorld(), pos1, pos2);

        Iterable<BlockPos> positions = BlockPos.betweenClosed(pos1, pos2);

        for (BlockPos pos : positions) {
            input.place(player.getCommandSenderWorld(), pos);
        }

        return cachedArea;
    }
}
