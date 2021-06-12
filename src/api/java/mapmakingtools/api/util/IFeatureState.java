package mapmakingtools.api.util;

import net.minecraftforge.fml.loading.FMLEnvironment;

public interface IFeatureState {

    default boolean isVisible() {
        switch (this.getFeatureState()) {
            case DEVELOPMENT:
                return !FMLEnvironment.production;
            case ALPHA:
                return !FMLEnvironment.production || true; // TODO or is in accepted clients
            default:
                return true;
        }
    }

    default boolean canUse() {
        switch (this.getFeatureState()) {
            case RELEASE:
            case BETA:
                return true;
            case ALPHA:
                return !FMLEnvironment.production || true; // TODO or is in accepted clients
            default:
                return !FMLEnvironment.production;
        }
    }

    default State getFeatureState() {
        return State.RELEASE;
    }
}
