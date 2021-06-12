package mapmakingtools.api.worldeditor;

import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface Action {

    public ICachedArea doAction(PlayerEntity player, ISelection selection, CachedBlock input);
}
