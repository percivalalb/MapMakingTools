package mapmakingtools.api.itemeditor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IItemAttributeClient {

    @Deprecated
    default void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {

    }

    default void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
        this.init(screen, add, update, pauseUpdates, stack.get(), x, y, width, height);
    }

    public void populateFrom(Screen screen, final ItemStack stack);

    default boolean shouldRenderTitle(Screen screen, final ItemStack stack) {
        return true;
    }

    default void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
    }

    default boolean writeAll(ItemStack stack, FriendlyByteBuf buffer) {
        return false;
    }

    default void clear(Screen screen) {
    }

    default void tick(Screen screen, long systemTimeMillis, Consumer<FriendlyByteBuf> update) {
    }

    default boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
        return true;
    }
}
