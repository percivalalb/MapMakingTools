package mapmakingtools.worldeditor.action;

import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.worldeditor.CachedCuboidArea;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class RoofAction implements Action {

    @Override
    public ICachedArea doAction(PlayerEntity player, ISelection selection, CachedBlock input) {
        BlockPos.Mutable pos1 = selection.getPrimaryPoint().toMutable();
        BlockPos.Mutable pos2 = selection.getSecondaryPoint().toMutable();
        pos1.setY(selection.getMaxY());
        pos2.setY(selection.getMaxY());

        ICachedArea cachedArea = CachedCuboidArea.from(player.getEntityWorld(), pos1, pos2);

        Iterable<BlockPos> positions = BlockPos.getAllInBoxMutable(pos1, pos2);

        for (BlockPos pos : positions) {
            input.place(player.getEntityWorld(), pos);
        }

        return cachedArea;
    }
}
