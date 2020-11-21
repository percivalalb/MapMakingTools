package mapmakingtools.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.client.screen.widget.component.TextComponentMakerWidget;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemNameAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            return stack.setDisplayName(new StringTextComponent(buffer.readString(128)));
        case 1:
            return stack.setDisplayName(new TranslationTextComponent(buffer.readString(128)));
        case 2:
            stack.clearCustomName();
            return stack;
        case 3:
            return stack.setDisplayName(buffer.readTextComponent());
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private TextComponentMakerWidget widgetMaker;
            private TextFieldWidget nameInput;
            private Button nameRemoval;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
                this.widgetMaker = new TextComponentMakerWidget(x + 3, y + 16, width - 6, height - 36);
                add.accept(this.widgetMaker);

                Button setBtn = new SmallButton(x + width / 2 - 100, y + height - 23, 200, 20, new TranslationTextComponent(getTranslationKey("button.set")), (btn) -> {
                    System.out.println("Send");
                    if (this.widgetMaker.hasTextComponent()) {
                        PacketBuffer buf = Util.createBuf();
                        buf.writeByte(3);
                        System.out.println("Send2");
                        buf.writeTextComponent(this.widgetMaker.getTextComponent());
                        update.accept(buf);
                    }
                });
                add.accept(setBtn);







                this.nameInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.nameInput, () -> stack.get().getDisplayName().getUnformattedComponentText());
                this.nameInput.setMaxStringLength(128);
                this.nameInput.setResponder(str -> {
                    if (!stack.get().hasDisplayName() && stack.get().getItem().getDisplayName(stack.get()).getUnformattedComponentText().equals(str)) {
                        return;
                    }

                    this.nameRemoval.active = true;
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(0);
                    buf.writeString(str, 128);
                    update.accept(buf);
                });

                this.nameRemoval = new Button(x + width / 2 - 100, y + 40, 200, 20, new TranslationTextComponent(getTranslationKey("button.remove.tag")), (btn) -> {
                    btn.active = false;
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(2);
                    update.accept(buf);
                });

              ///  add.accept(this.nameInput);
                //add.accept(this.nameRemoval);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                WidgetUtil.setTextQuietly(this.nameInput, stack.getDisplayName().getString());
                this.nameRemoval.active = stack.hasDisplayName();

                this.widgetMaker.populateFrom(stack.getDisplayName());
            }

            @Override
            public void clear(Screen screen) {
                this.nameInput = null;
                this.nameRemoval = null;
                this.widgetMaker = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return !newStack.getDisplayName().equals(oldStack.getDisplayName());
            }
        };
    }

}
