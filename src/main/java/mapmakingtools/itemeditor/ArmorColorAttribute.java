package mapmakingtools.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ColorPickerWidget;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;

public class ArmorColorAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item instanceof IDyeableArmorItem;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            if (stack.getItem() instanceof IDyeableArmorItem) {
                IDyeableArmorItem dye = (IDyeableArmorItem) stack.getItem();
                dye.setColor(stack, buffer.readInt());
            }

            return stack;
        case 1:
            if (stack.getItem() instanceof IDyeableArmorItem) {
                IDyeableArmorItem dye = (IDyeableArmorItem) stack.getItem();
                dye.removeColor(stack);
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
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.colorPicker = new ColorPickerWidget(x + 3, y + 15, width - 106, height - 20, this.colorPicker);

                this.setBtn = new Button(x + width - 100, y + 70, 40, 20, new StringTextComponent("Set"), (btn) -> {
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(0);
                    buf.writeInt(this.colorPicker.getColorPickedRGB());
                    update.accept(buf);
                });

                this.removeBtn = new Button(x + width - 100, y + 93, 50, 20, new StringTextComponent("Remove"), BufferFactory.ping(1, update));

                add.accept(this.colorPicker);
                add.accept(this.setBtn);
                add.accept(this.removeBtn);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.removeBtn.active = stack.getItem() instanceof IDyeableArmorItem && ((IDyeableArmorItem)stack.getItem()).hasColor(stack);

                if (stack.getItem() instanceof IDyeableArmorItem) {
                    if (this.removeBtn.active) {
                        this.colorPicker.setColor(((IDyeableArmorItem)stack.getItem()).getColor(stack));
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
                return true; // TODO
            }
        };
    }

}
