package mapmakingtools.itemeditor;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.util.State;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.client.screen.widget.component.TextComponentMakerWidget;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemNameAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            return stack.setHoverName(Component.literal(buffer.readUtf(128)));
        case 1:
            return stack.setHoverName(Component.translatable(buffer.readUtf(128)));
        case 2:
            stack.resetHoverName();
            return stack;
        case 3:
            return stack.setHoverName(buffer.readComponent());
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private TextComponentMakerWidget widgetMaker;
            private EditBox nameInput;
            private Button nameRemoval;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
                this.widgetMaker = new TextComponentMakerWidget(x + 3, y + 16, width - 6, height - 36);
                add.accept(this.widgetMaker);

                Button setBtn = new SmallButton(x + width / 2 - 100, y + height - 23, 200, 20, Component.translatable(getTranslationKey("button.set")), (btn) -> {
                    System.out.println("Send");
                    if (this.widgetMaker.hasTextComponent()) {
                        FriendlyByteBuf buf = Util.createBuf();
                        buf.writeByte(3);
                        System.out.println("Send2");
                        buf.writeComponent(this.widgetMaker.getTextComponent());
                        update.accept(buf);
                    }
                });
                add.accept(setBtn);







                this.nameInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.nameInput, () -> stack.get().getHoverName().getContents());
                this.nameInput.setMaxLength(128);
                this.nameInput.setResponder(str -> {
                    if (!stack.get().hasCustomHoverName() && stack.get().getItem().getName(stack.get()).getContents().equals(str)) {
                        return;
                    }

                    this.nameRemoval.active = true;
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    buf.writeUtf(str, 128);
                    update.accept(buf);
                });

                this.nameRemoval = new Button(x + width / 2 - 100, y + 40, 200, 20, Component.translatable(getTranslationKey("button.remove.tag")), (btn) -> {
                    btn.active = false;
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(2);
                    update.accept(buf);
                });

              ///  add.accept(this.nameInput);
                //add.accept(this.nameRemoval);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                WidgetUtil.setTextQuietly(this.nameInput, stack.getHoverName().getString());
                this.nameRemoval.active = stack.hasCustomHoverName();

                this.widgetMaker.populateFrom(stack.getHoverName());
            }

            @Override
            public void clear(Screen screen) {
                this.nameInput = null;
                this.nameRemoval = null;
                this.widgetMaker = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return !newStack.getHoverName().equals(oldStack.getHoverName());
            }
        };
    }

    @Override
    public State getFeatureState() {
        return State.ALPHA;
    }
}
