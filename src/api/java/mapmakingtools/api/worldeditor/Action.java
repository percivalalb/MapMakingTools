package mapmakingtools.api.worldeditor;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface Action {

    public ICachedArea doAction(Player player, ISelection selection, CachedBlock input);
}
