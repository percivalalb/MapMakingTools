package mapmakingtools.client.data;

import mapmakingtools.MapMakingTools;
import mapmakingtools.lib.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheld(MapMakingTools.WRENCH);
    }

    private String name(Supplier<? extends ItemLike> item) {
        return ForgeRegistries.ITEMS.getKey(item.get().asItem()).getPath();
    }

    private ResourceLocation itemTexture(Supplier<? extends ItemLike> item) {
        return modLoc(ModelProvider.ITEM_FOLDER + "/" + name(item));
    }

    private ItemModelBuilder handheld(Supplier<? extends ItemLike> item) {
        return handheld(item, itemTexture(item));
    }

    private ItemModelBuilder handheld(Supplier<? extends ItemLike> item, ResourceLocation texture) {
        return getBuilder(name(item)).parent(new ModelFile.UncheckedModelFile(ModelProvider.ITEM_FOLDER + "/handheld")).texture("layer0", texture);
    }
}
