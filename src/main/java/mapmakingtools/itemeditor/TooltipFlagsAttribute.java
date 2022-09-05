package mapmakingtools.itemeditor;

import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.TickButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TooltipFlagsAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch (buffer.readByte()) {
            case 0:
                int index = buffer.readInt();
                CompoundTag tag = NBTUtil.getOrCreateTag(stack);
                int flagBinaryString = tag.getInt("HideFlags") ^ (1 << index);

                if (flagBinaryString == 0) {
                    NBTUtil.removeTag(stack, "HideFlags", Tag.TAG_ANY_NUMERIC);
                    NBTUtil.removeTagIfEmpty(stack);
                } else {
                    tag.putInt("HideFlags", tag.getInt("HideFlags") ^ (1 << index));
                }

                return stack;
            case 1:
                boolean showAll = buffer.readBoolean();

                if (showAll) {
                    NBTUtil.removeTag(stack, "HideFlags", Tag.TAG_ANY_NUMERIC);
                    NBTUtil.removeTagIfEmpty(stack);
                } else {
                    CompoundTag tag1 = NBTUtil.getOrCreateTag(stack);
                    tag1.putInt("HideFlags", (int) Math.pow(2, 7) - 1);
                }

                return stack;
            default:
                throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private FlexibleArrayList<TickButton> flagTickButtons = new FlexibleArrayList<>(7);
            private TickButton flagAllButton;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {

                for (int i = 0; i < 7; i++) {
                    final int index = i; // creates final variable

                    TickButton tickBtn = WidgetFactory.getTickbox(x + 102, y + 16 + 22 * i, this.flagTickButtons.getSafe(i), () -> true, (btn) -> {
                        this.flagAllButton.setTicked(this.allTicked(((TickButton) btn).isTicked()));
                        FriendlyByteBuf buf = Util.createBuf();
                        buf.writeByte(0);
                        buf.writeInt(index);
                        update.accept(buf);
                    });

                    this.flagTickButtons.set(i, tickBtn);
                    add.accept(tickBtn);
                }

                this.flagAllButton = WidgetFactory.getTickbox(x + 112, y + height - 29, this.flagAllButton, () -> true, (btn) -> {
                    boolean ticked = ((TickButton) btn).isTicked();
                    for (TickButton button : this.flagTickButtons) {
                        button.setTicked(ticked);
                    }

                    BufferFactory.createBoolean(1, update).accept(ticked);
                });

                add.accept(this.flagAllButton);
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
                Font font = screen.getMinecraft().font;
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.enchantment")), x + 6, y + 25, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.attribute_modifier")), x + 6, y + 47, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.unbreakable")), x + 6, y + 69, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.block_destroy")), x + 6, y + 91, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.can_place_on")), x + 6, y + 113, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.normal_info")), x + 6, y + 135, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.dye")), x + 6, y + 157, 10526880);
                font.draw(stackIn, Component.translatable(getTranslationKey("flag.all")), x + 6, y + height - 22, 16777120);
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

}
