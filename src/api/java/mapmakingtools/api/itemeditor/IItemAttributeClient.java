package mapmakingtools.api.itemeditor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public interface IItemAttributeClient {

    default void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {

    }

    // Turn into item stack supplier
    default void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
        this.init(screen, add, update, pauseUpdates, stack.get(), x, y, width, height);
    }

    public void populateFrom(Screen screen, final ItemStack stack);

    default boolean shouldRenderTitle(Screen screen, final ItemStack stack) { return true; }

    default void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {}

    default boolean writeAll(ItemStack stack, PacketBuffer buffer) { return false; }

    default void clear(Screen screen) {}

    default void tick(Screen screen, long systemTimeMillis, Consumer<PacketBuffer> update) {}

    default boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
        return true;
    }
}
