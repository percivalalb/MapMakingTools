package mapmakingtools.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

public class BookDetailsAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item == Items.WRITTEN_BOOK;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            NBTUtil.getOrCreateTag(stack).putString("title", buffer.readString(128));
            return stack;
        case 1:
            NBTUtil.getOrCreateTag(stack).putString("author", buffer.readString(128));
            return stack;
        case 2:
            int generation = buffer.readInt();
            if (generation == 0) {
                if (NBTUtil.hasTag(stack, "generation", Constants.NBT.TAG_ANY_NUMERIC)) {
                    stack.getTag().remove("generation");
                    NBTUtil.removeTagIfEmpty(stack);
                }
            } else {
                NBTUtil.getOrCreateTag(stack).putInt("generation", generation);
            }
            return stack;
        case 3:
            if (!stack.getItem().delegate.equals(Items.WRITTEN_BOOK.delegate)) {
                throw new IllegalStateException("Book is not a written book");
            }

            ItemStack book = new ItemStack(Items.WRITABLE_BOOK, stack.getCount());
            book.setTag(stack.getTag());
            if (NBTUtil.hasTag(stack, "pages", Constants.NBT.TAG_LIST)) {
                ListNBT listNBT = book.getTag().getList("pages", Constants.NBT.TAG_STRING);

                for (int i = 0; i < listNBT.size(); ++i) {
                    String s = listNBT.getString(i);

                    ITextComponent textComponent = ITextComponent.Serializer.getComponentFromJson(s);
                    listNBT.set(i, StringNBT.valueOf(textComponent.getString()));
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

            private TextFieldWidget bookNameInput;
            private TextFieldWidget authorInput;
            private TextFieldWidget generationInput;
            private Button convertBackBtn;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.bookNameInput = WidgetFactory.getTextField(screen, x + 2, y + 28, 80, 13, this.bookNameInput, () -> NBTUtil.hasTag(stack, "title", Constants.NBT.TAG_STRING) ? stack.getTag().getString("title") : "");
                this.bookNameInput.setMaxStringLength(128);
                this.bookNameInput.setResponder(BufferFactory.createString(0, update));

                this.authorInput = WidgetFactory.getTextField(screen, x + 86, y + 28, 80, 13, this.authorInput, () -> NBTUtil.hasTag(stack, "author", Constants.NBT.TAG_STRING) ? stack.getTag().getString("author") : "");
                this.authorInput.setMaxStringLength(128);
                this.authorInput.setResponder(BufferFactory.createString(1, update));

                this.generationInput = WidgetFactory.getTextField(screen, x + 170, y + 28, 80, 13, this.generationInput, () -> NBTUtil.hasTag(stack, "generation", Constants.NBT.TAG_ANY_NUMERIC) ? stack.getTag().getInt("generation") : 0);
                this.generationInput.setMaxStringLength(1);
                this.generationInput.setResponder(BufferFactory.createInteger(2, Strings::isNullOrEmpty, update));
                this.generationInput.setValidator(Util.NON_NEGATIVE_NUMBER_INPUT_PREDICATE);

                this.convertBackBtn = new Button(x + 2, y + 48, 200, 20, new StringTextComponent("Convert back to writable book"), BufferFactory.ping(3, update));

                add.accept(this.bookNameInput);
                add.accept(this.authorInput);
                add.accept(this.generationInput);
                add.accept(this.convertBackBtn);
            }

            @Override
            public void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {
                FontRenderer font = screen.getMinecraft().fontRenderer;
                font.drawString(stackIn, "Title", x + 2, y + 17, -1);
                font.drawString(stackIn, "Author", x + 86, y + 17, -1);
                font.drawString(stackIn, "Generation", x + 170, y + 17, -1);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (NBTUtil.hasTag(stack, "title", Constants.NBT.TAG_STRING)) {
                    WidgetUtil.setTextQuietly(this.bookNameInput, stack.getTag().getString("title"));
                }

                if (NBTUtil.hasTag(stack, "author", Constants.NBT.TAG_STRING)) {
                    WidgetUtil.setTextQuietly(this.authorInput, stack.getTag().getString("author"));
                }

                if (NBTUtil.hasTag(stack, "generation", Constants.NBT.TAG_ANY_NUMERIC)) {
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
