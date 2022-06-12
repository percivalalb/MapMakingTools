package mapmakingtools.client.screen.widget;

import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Map;

public class ToggleBoxRegistryList<T> extends ToggleBoxList<Map.Entry<ResourceKey<T>, T>> {

    public ToggleBoxRegistryList(int xIn, int yIn, int widthIn, int heightIn, @Nullable ToggleBoxList<Map.Entry<ResourceKey<T>, T>> previous) {
        super(xIn, yIn, widthIn, heightIn, previous);
    }

    public ToggleBoxRegistryList<T> setValues(IForgeRegistry<T> registry, @Nullable ToggleBoxRegistryList<T> previous) {
        this.clear(); // clear old values

        int i = 2;
        for (Map.Entry<ResourceKey<T>, T> value : registry.getEntries()) {
            ToggleBoxWidget<Map.Entry<ResourceKey<T>, T>> box = new ToggleBoxWidget<>(this.x + 2, this.y + i, null, () -> value, this.toggleGroup::buttonClicked);
            box.setDisplayString(t -> t.getKey().location());

            this.widgets.add(box);
            i += 10;
            this.noElements++;
        }

        this.hiddenHeight = Math.max(0, i + 2 - this.height);

        if (previous != null) {
            this.clampScrollOffset(previous.scrollOffset);
        }

        return this;
    }
}
