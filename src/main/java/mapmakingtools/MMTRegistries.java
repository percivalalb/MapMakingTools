package mapmakingtools;

import com.google.common.collect.Maps;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.itemeditor.Registries;
import mapmakingtools.lib.Constants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MMTRegistries {

    private static final ResourceLocation SERVER_TO_CLIENT_MAP = new ResourceLocation(Constants.MOD_ID, "client_attribute");

    public static class AttributeCallbacks implements IForgeRegistry.AddCallback<IItemAttribute>, IForgeRegistry.ClearCallback<IItemAttribute>, IForgeRegistry.BakeCallback<IItemAttribute>, IForgeRegistry.CreateCallback<IItemAttribute> {

        static final AttributeCallbacks INSTANCE = new AttributeCallbacks();

        @SuppressWarnings("unchecked")
        @Override
        public void onAdd(IForgeRegistryInternal<IItemAttribute> owner, RegistryManager stage, int id, ResourceKey<IItemAttribute> key, IItemAttribute newAttribute, @Nullable IItemAttribute oldAttribute) {
            owner.getSlaveMap(SERVER_TO_CLIENT_MAP, HashMap.class).put(key, DistExecutor.callWhenOn(Dist.CLIENT, newAttribute.client()));
        }

        @Override
        public void onClear(IForgeRegistryInternal<IItemAttribute> owner, RegistryManager stage) {
            owner.getSlaveMap(SERVER_TO_CLIENT_MAP, HashMap.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<IItemAttribute> owner, RegistryManager stage) {
            owner.setSlaveMap(SERVER_TO_CLIENT_MAP, Maps.newHashMap());
        }

        @Override
        public void onBake(IForgeRegistryInternal<IItemAttribute> owner, RegistryManager stage)
        {

        }
    }

    @SuppressWarnings("unchecked")
    public static HashMap<ResourceKey<IItemAttribute>, IItemAttributeClient> getClientMapping() {
        return Registries.ITEM_ATTRIBUTES.get().getSlaveMap(SERVER_TO_CLIENT_MAP, HashMap.class);
    }
}
