package mapmakingtools.worldeditor.action;

import mapmakingtools.api.worldeditor.Action;
import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.worldeditor.CachedCuboidArea;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Clearable;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class MazeAction implements Action {

    @Override
    public ICachedArea doAction(Player player, ISelection selection, CachedBlock input) {
        Level world = player.getCommandSenderWorld();

        ICachedArea cachedArea = CachedCuboidArea.from(world, selection);

        Iterable<BlockPos> positions = BlockPos.betweenClosed(selection.getPrimaryPoint(), selection.getSecondaryPoint());

        Map<BlockPos, Integer> groups = new HashMap<>();
        int group = 0;
        for (BlockPos pos : positions) {
            if ((pos.getX() - selection.getMinX()) % 2 == 1 && (pos.getZ() - selection.getMinZ()) % 2 == 1 && pos.getX() != selection.getMaxX() && pos.getZ() != selection.getMaxZ()) {
                Clearable.tryClear(world.getBlockEntity(pos));
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                groups.put(new BlockPos(pos.getX(), 0, pos.getZ()), group);
                group += 1;
            }
            else {
                input.place(world, pos);
            }
        }

        if (groups.isEmpty()) {
            return cachedArea;
        }

        while (true) {

            BlockPos[] keys = groups.keySet().toArray(new BlockPos[groups.size()]);
            BlockPos intersectionPos = keys[world.random.nextInt(groups.size())];
            int thisId = groups.get(intersectionPos);

            // Checks if all groups are the same - all pathways are connected
            boolean done = true;
            for (BlockPos spot : groups.keySet()) {
                if (groups.get(spot) != thisId) {
                    done = false;
                    break;
                }
            }

            if (done) {
                break;
            }

            Direction dir = Direction.from2DDataValue(world.random.nextInt(4));
            BlockPos.MutableBlockPos posHere = intersectionPos.mutable().move(0, selection.getMinY(), 0).move(dir, 1);
            if (world.isEmptyBlock(posHere)) {
                continue;
            }

            BlockPos nextIntersectionPos = intersectionPos.relative(dir, 2);

            // Spot it wants to connect to doesn't exist
            if (!groups.containsKey(nextIntersectionPos)) {
                continue;
            }

            int oldId = groups.get(nextIntersectionPos);

            if (oldId == thisId) { // Already Connected
                continue;
            }

            // Set the old group to the next one combining the groups
            for (BlockPos spot : groups.keySet()) {
                if (groups.get(spot) == oldId) {
                    groups.put(spot, thisId);
                }
            }

            // Clear pathway
            for (int y = selection.getMinY(); y <= selection.getMaxY(); y++) {
                Clearable.tryClear(world.getBlockEntity(posHere));
                world.setBlock(posHere, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                posHere.move(0, 1, 0);
            }
        }

        return cachedArea;
    }
}
