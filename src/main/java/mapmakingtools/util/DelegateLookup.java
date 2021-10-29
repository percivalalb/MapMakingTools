package mapmakingtools.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IRegistryDelegate;

public class DelegateLookup<T> implements IRegistryDelegate<T> {

    @Override
    public T get() {
        return null;
    }

    @Override
    public ResourceLocation name() {
        return null;
    }

    @Override
    public Class<T> type() {
        return null;
    }
}
