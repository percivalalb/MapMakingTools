package mapmakingtools.api.util;

import net.minecraftforge.fml.loading.FMLEnvironment;

public interface IFeatureState {

    default boolean isVisible() {
        switch (this.getFeatureState()) {
            case DEVELOPMENT:
                return !FMLEnvironment.production;
            default:
                return true;
        }
    }

    default boolean canUse() {
        switch (this.getFeatureState()) {
            case RELEASE:
                return true;
            default:
                return !FMLEnvironment.production;
        }
    }

    default State getFeatureState() {
        return State.RELEASE;
    }
}
