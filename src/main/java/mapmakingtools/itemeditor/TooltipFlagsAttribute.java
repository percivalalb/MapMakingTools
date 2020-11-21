package mapmakingtools.itemeditor;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.TickButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

public class TooltipFlagsAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            int index = buffer.readInt();
            CompoundNBT tag = NBTUtil.getOrCreateTag(stack);
            int flagBinaryString = tag.getInt("HideFlags") ^ (1 << index);

            if (flagBinaryString == 0) {
                NBTUtil.removeTag(stack, "HideFlags", Constants.NBT.TAG_ANY_NUMERIC);
                NBTUtil.removeTagIfEmpty(stack);
            } else {
                tag.putInt("HideFlags", tag.getInt("HideFlags") ^ (1 << index));
            }

            return stack;
        case 1:
            boolean showAll = buffer.readBoolean();

            if (showAll) {
                NBTUtil.removeTag(stack, "HideFlags", Constants.NBT.TAG_ANY_NUMERIC);
                NBTUtil.removeTagIfEmpty(stack);
            } else {
                CompoundNBT tag1 = NBTUtil.getOrCreateTag(stack);
                tag1.putInt("HideFlags", (int) Math.pow(2, 6) - 1);
            }

            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    public static class FlexibleArrayList<T> extends ArrayList<T> {

        private static final long serialVersionUID = 1L;

        public FlexibleArrayList(int size) {
            super(size);
        }

        @Override
        public T set(int index, T element) {
            if (this.size() == index) {
                this.add(element);
                return null;
            } else {
                return super.set(index, element);
            }
        }

        public T getSafe(int index) {
            if (index < 0 || index >= this.size()) {
                return null;
            }
            return this.get(index);
        }
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private FlexibleArrayList<TickButton> flagTickButtons = new FlexibleArrayList<>(6);
            private TickButton flagAllButton;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {

                for (int i = 0; i < 6; i++) {
                    final int index = i; // creates final variable

                    TickButton tickBtn = WidgetFactory.getTickbox(x + 102, y + 16 + 22 * i, this.flagTickButtons.getSafe(i), () -> true, (btn) -> {
                        this.flagAllButton.setTicked(this.allTicked(((TickButton)btn).isTicked()));
                        PacketBuffer buf = Util.createBuf();
                        buf.writeByte(0);
                        buf.writeInt(index);
                        update.accept(buf);
                    });

                    this.flagTickButtons.set(i, tickBtn);
                    add.accept(tickBtn);
                }

                this.flagAllButton = WidgetFactory.getTickbox(x + 112, y + height - 29, this.flagAllButton, () -> true, (btn) -> {
                    boolean ticked = ((TickButton)btn).isTicked();
                    for (TickButton button : this.flagTickButtons) {
                        button.setTicked(ticked);
                    }

                    BufferFactory.createBoolean(1, update).accept(ticked);
                });

                add.accept(this.flagAllButton);
            }

            @Override
            public void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {
                FontRenderer font = screen.getMinecraft().fontRenderer;
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.enchantment")), x + 6, y + 25, 10526880);
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.attribute_modifier")), x + 6, y + 47, 10526880);
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.unbreakable")), x + 6, y + 69, 10526880);
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.block_destroy")), x + 6, y + 91, 10526880);
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.can_place_on")), x + 6, y + 113, 10526880);
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.normal_info")), x + 6, y + 135, 10526880);
                font.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey("flag.all")), x + 6, y + height - 22, 16777120);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                int binaryFlags = NBTUtil.getInt(stack, "HideFlags");

                boolean showAllInfo = true;
                for (int i = 0; i < this.flagTickButtons.size(); i++) {
                    boolean showInfo = (binaryFlags & (1 << i)) == 0;
                    showAllInfo = showAllInfo && showInfo;
                    this.flagTickButtons.get(i).setTicked(showInfo);
                }

                this.flagAllButton.setTicked(showAllInfo);
            }

            @Override
            public void clear(Screen screen) {
                this.flagTickButtons.clear();
                this.flagAllButton = null;
            }

            public boolean allTicked(boolean change) {
                if (!change) {
                    return false;
                }

                for (TickButton button : this.flagTickButtons) {
                    if (!button.isTicked()) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return NBTUtil.getInt(newStack, "HideFlags") != NBTUtil.getInt(oldStack, "HideFlags");
            }
        };
    }

}
