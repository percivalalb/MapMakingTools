package mapmakingtools.worldeditor.action;

import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

public class WallAction implements Action {

    @Override
    public ICachedArea doAction(Player player, ISelection selection, CachedBlock input) {
        BlockPos firstPos = selection.getPrimaryPoint();
        BlockPos secondPos = selection.getSecondaryPoint();
        Iterable<BlockPos> positions = BlockPos.betweenClosed(Math.min(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.min(firstPos.getZ(), secondPos.getZ()), Math.max(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.max(firstPos.getZ(), secondPos.getZ()));

        for (BlockPos pos : positions) {
            input.place(player.getCommandSenderWorld(), pos);
        }
        return null;
    }
}
