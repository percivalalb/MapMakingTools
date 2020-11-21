package mapmakingtools.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.scroll.TextScrollPane;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

public class NBTViewer extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
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
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.nbtRemoval = new Button(x + 3, y + 13, 120, 20, new TranslationTextComponent(getTranslationKey("button.remove")), (btn) -> {
                    btn.active = false;
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(0);
                    update.accept(buf);
                });

                this.nbtTextPane = new TextScrollPane(x + 3, y + 38, width - 6, height - 41);

                add.accept(this.nbtRemoval);
                add.accept(this.nbtTextPane);
            }

            @Override
            public void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {

            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.nbtRemoval.active = stack.hasTag();
                if (stack.hasTag()) {
                    this.nbtTextPane.setText(stack.getTag().toFormattedComponent("    ", 0));
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
