package mapmakingtools.itemeditor;

import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.scroll.TextScrollPane;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NBTViewer extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            stack.setTag(null);
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private Button nbtRemoval;
            private TextScrollPane nbtTextPane;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, Supplier<ItemStack> stack, int x, int y, int width, int height) {
                this.nbtRemoval = new Button(x + 3, y + 13, 120, 20, Component.translatable(getTranslationKey("button.remove")), (btn) -> {
                    btn.active = false;
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    update.accept(buf);
                });

                this.nbtTextPane = new TextScrollPane(x + 3, y + 38, width - 6, height - 41);

                add.accept(this.nbtRemoval);
                add.accept(this.nbtTextPane);
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {

            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.nbtRemoval.active = stack.hasTag();
                if (stack.hasTag()) {
                    assert stack.getTag() != null;
                    this.nbtTextPane.setText(new TextComponentTagVisitor("    ", 0).visit(stack.getTag()));
                } else {
                    this.nbtTextPane.setText(null);
                }
            }

            @Override
            public void clear(Screen screen) {
                this.nbtRemoval = null;
                this.nbtTextPane = null;
            }
        };
    }

}
