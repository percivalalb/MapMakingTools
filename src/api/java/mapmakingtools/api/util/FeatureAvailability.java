package mapmakingtools.api.util;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class FeatureAvailability {

    public static boolean canEdit(@Nullable PlayerEntity player) {
        if(player == null) {
            return false;
        }

        boolean isCreativeMode = player.abilities.instabuild;
        boolean isOpped = player.hasPermissions(2);

        return isCreativeMode && isOpped;
    }
}
