package mapmakingtools.itemeditor;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BookDetailsAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item == Items.WRITTEN_BOOK;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer, Player player) {
        switch(buffer.readByte()) {
        case 0:
            NBTUtil.getOrCreateTag(stack).putString("title", buffer.readUtf(128));
            return stack;
        case 1:
            NBTUtil.getOrCreateTag(stack).putString("author", buffer.readUtf(128));
            return stack;
        case 2:
            int generation = buffer.readInt();
            if (generation == 0) {
                if (NBTUtil.hasTag(stack, "generation", Tag.TAG_ANY_NUMERIC)) {
                    stack.getTag().remove("generation");
                    NBTUtil.removeTagIfEmpty(stack);
                }
            } else {
                NBTUtil.getOrCreateTag(stack).putInt("generation", generation);
            }
            return stack;
        case 3:
            if (!stack.is(Items.WRITTEN_BOOK)) {
                throw new IllegalStateException("Book is not a written book");
            }

            ItemStack book = new ItemStack(Items.WRITABLE_BOOK, stack.getCount());
            book.setTag(stack.getTag());
            if (NBTUtil.hasTag(stack, "pages", Tag.TAG_LIST)) {
                ListTag listNBT = book.getTag().getList("pages", Tag.TAG_STRING);

                for (int i = 0; i < listNBT.size(); ++i) {
                    String s = listNBT.getString(i);

                    Component textComponent = Component.Serializer.fromJson(s);
                    listNBT.set(i, StringTag.valueOf(textComponent.getString()));
                }

            }

            return book;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private EditBox bookNameInput;
            private EditBox authorInput;
            private EditBox generationInput;
            private Button convertBackBtn;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
                this.bookNameInput = WidgetFactory.getTextField(screen, x + 2, y + 28, 80, 13, this.bookNameInput, () -> NBTUtil.hasTag(stack.get(), "title", Tag.TAG_STRING) ? stack.get().getTag().getString("title") : "");
                this.bookNameInput.setMaxLength(128);
                this.bookNameInput.setResponder(BufferFactory.createString(0, update));

                this.authorInput = WidgetFactory.getTextField(screen, x + 86, y + 28, 80, 13, this.authorInput, () -> NBTUtil.hasTag(stack.get(), "author", Tag.TAG_STRING) ? stack.get().getTag().getString("author") : "");
                this.authorInput.setMaxLength(128);
                this.authorInput.setResponder(BufferFactory.createString(1, update));

                this.generationInput = WidgetFactory.getTextField(screen, x + 170, y + 28, 80, 13, this.generationInput, () -> NBTUtil.hasTag(stack.get(), "generation", Tag.TAG_ANY_NUMERIC) ? stack.get().getTag().getInt("generation") : 0);
                this.generationInput.setMaxLength(1);
                this.generationInput.setResponder(BufferFactory.createInteger(2, Strings::isNullOrEmpty, update));
                this.generationInput.setFilter(Util.NON_NEGATIVE_NUMBER_INPUT_PREDICATE);

                this.convertBackBtn = new Button(x + 2, y + 48, 200, 20, Component.literal("Convert back to writable book"), BufferFactory.ping(3, update));

                add.accept(this.bookNameInput);
                add.accept(this.authorInput);
                add.accept(this.generationInput);
                add.accept(this.convertBackBtn);
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
                Font font = screen.getMinecraft().font;
                font.draw(stackIn, "Title", x + 2, y + 17, -1);
                font.draw(stackIn, "Author", x + 86, y + 17, -1);
                font.draw(stackIn, "Generation", x + 170, y + 17, -1);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (NBTUtil.hasTag(stack, "title", Tag.TAG_STRING)) {
                    WidgetUtil.setTextQuietly(this.bookNameInput, stack.getTag().getString("title"));
                }

                if (NBTUtil.hasTag(stack, "author", Tag.TAG_STRING)) {
                    WidgetUtil.setTextQuietly(this.authorInput, stack.getTag().getString("author"));
                }

                if (NBTUtil.hasTag(stack, "generation", Tag.TAG_ANY_NUMERIC)) {
                    WidgetUtil.setTextQuietly(this.generationInput, String.valueOf(stack.getTag().getInt("generation")));
                }
            }

            @Override
            public void clear(Screen screen) {
                this.bookNameInput = null;
                this.authorInput = null;
                this.generationInput = null;
                this.convertBackBtn = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                if (newStack.hasTag() != oldStack.hasTag()) {
                    return true;
                } else if (!newStack.hasTag()) {
                    return false;
                }

                return !newStack.getTag().getString("title").equals(oldStack.getTag().getString("title")) ||
                        !newStack.getTag().getString("author").equals(oldStack.getTag().getString("author")) ||
                        newStack.getTag().getInt("generation") != oldStack.getTag().getInt("generation");
            }
        };
    }
}
