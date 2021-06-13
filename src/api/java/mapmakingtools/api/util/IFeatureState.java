package mapmakingtools.api.util;

import net.minecraftforge.fml.loading.FMLEnvironment;

public interface IFeatureState {

    /**
     * If the feature should be visible to the current player
     * @return If the feature is visible
     */
    default boolean isVisible() {
        switch (this.getFeatureState()) {
            case DEVELOPMENT:
                return !FMLEnvironment.production;
            case ALPHA:
                return !FMLEnvironment.production; // TODO or is in accepted clients
            default:
                return true;
        }
    }

    /**
     * If the feature can be used by the current place and isn't just a placeholder.
     * If this method returns true then {@link IFeatureState::isVisible} must also
     * return true.
     *
     * @return If the feature can be used
     */
    default boolean canUse() {
        switch (this.getFeatureState()) {
            case RELEASE:
            case BETA:
                return true;
            case ALPHA:
                return !FMLEnvironment.production; // TODO or is in accepted clients
            default:
                return !FMLEnvironment.production;
        }
    }

    /**
     * Determines the values returned by {@link IFeatureState::isVisible} and
     * {@link IFeatureState::canUse}
     *
     * @return The current state of this feature
     */
    default State getFeatureState() {
        return State.RELEASE;
    }
}
