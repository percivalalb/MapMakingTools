package mapmakingtools.itemeditor;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ColorPickerWidget;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArmorColorAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item instanceof DyeableLeatherItem;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            if (stack.getItem() instanceof DyeableLeatherItem) {
                DyeableLeatherItem dye = (DyeableLeatherItem) stack.getItem();
                dye.setColor(stack, buffer.readInt());
            }

            return stack;
        case 1:
            if (stack.getItem() instanceof DyeableLeatherItem) {
                DyeableLeatherItem dye = (DyeableLeatherItem) stack.getItem();
                dye.clearColor(stack);
                NBTUtil.removeTagIfEmpty(stack);
            }

            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private ColorPickerWidget colorPicker;
            private Button setBtn;
            private Button removeBtn;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, Supplier<ItemStack> stack, int x, int y, int width, int height) {
                this.colorPicker = new ColorPickerWidget(x + 3, y + 15, width - 106, height - 20, this.colorPicker);

                this.setBtn = new Button(x + width - 100, y + 70, 40, 20, Component.literal("Set"), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    buf.writeInt(this.colorPicker.getColorPickedRGB());
                    update.accept(buf);
                });

                this.removeBtn = new Button(x + width - 100, y + 93, 50, 20, Component.literal("Remove"), BufferFactory.ping(1, update));

                add.accept(this.colorPicker);
                add.accept(this.setBtn);
                add.accept(this.removeBtn);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.removeBtn.active = stack.getItem() instanceof DyeableLeatherItem && ((DyeableLeatherItem)stack.getItem()).hasCustomColor(stack);

                if (stack.getItem() instanceof DyeableLeatherItem) {
                    if (this.removeBtn.active) {
                        this.colorPicker.setColor(((DyeableLeatherItem)stack.getItem()).getColor(stack));
                    }
                }
            }

            @Override
            public void clear(Screen screen) {
                //this.damageInput = null;
                this.setBtn = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                Item newItem = newStack.getItem();
                Item oldItem = oldStack.getItem();
                if (newItem instanceof DyeableLeatherItem && oldItem instanceof DyeableLeatherItem) {
                    return ((DyeableLeatherItem) newItem).getColor(newStack) != ((DyeableLeatherItem) oldItem).getColor(oldStack);
                }

                return false;
            }
        };
    }

}
