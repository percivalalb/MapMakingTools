package mapmakingtools.itemeditor;

import com.google.common.base.Strings;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StackSizeAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            stack.setCount(buffer.readInt());
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private EditBox stackSizeInput;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.stackSizeInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.stackSizeInput, stack::getCount);
                this.stackSizeInput.setMaxLength(6);
                this.stackSizeInput.setResponder(BufferFactory.createInteger(0, Strings::isNullOrEmpty, update));
                this.stackSizeInput.setFilter(Util.BYTE_INPUT_PREDICATE);

                add.accept(this.stackSizeInput);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (!Strings.isNullOrEmpty(this.stackSizeInput.getValue())) {
                    WidgetUtil.setTextQuietly(this.stackSizeInput, String.valueOf(stack.getCount()));
                }
            }

            @Override
            public void clear(Screen screen) {
                this.stackSizeInput = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return newStack.getCount() != oldStack.getCount();
            }
        };
    }
}
