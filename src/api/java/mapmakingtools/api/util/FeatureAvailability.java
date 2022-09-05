package mapmakingtools.api.util;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class FeatureAvailability {

    public static boolean canEdit(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        boolean isCreativeMode = player.getAbilities().instabuild;
        boolean isOpped = player.hasPermissions(2);

        return isCreativeMode && isOpped;
    }
}
