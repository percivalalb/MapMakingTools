package mapmakingtools.itemeditor;

import com.google.common.collect.Lists;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.util.State;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoreAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            CompoundTag display = stack.getOrCreateTagElement("display");
            ListTag list = new ListTag();
            int size = buffer.readInt();
            for (int i = 0; i < size; i++) {
                list.add(StringTag.valueOf(Component.Serializer.toJson(new TextComponent(buffer.readUtf(256)))));
            }

            display.put("Lore", list);
            return stack;
        case 1:
            if (NBTUtil.hasTagInSubCompound(stack, "display", "Lore", Tag.TAG_LIST)) {
                ListTag list2 = stack.getTag().getCompound("display").getList("Lore", Tag.TAG_STRING);
                int index = buffer.readInt();
                if (index >= 0 && index < list2.size()) {
                    list2.remove(index);
                }
            }
            return stack;
        case 2:
            NBTUtil.removeTagFromSubCompound(stack, "display", Tag.TAG_LIST, "Lore");
            NBTUtil.removeTagIfEmpty(stack);
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private int MAX_LINES = 10;
            public int lines = 0;

            private List<EditBox> lineInput = Lists.newArrayList();
            private List<Button> removeInput = Lists.newArrayList();
            private Button addBtn;
            private Button btnRemove;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {

                for (int i = 0; i < MAX_LINES; i++) {
                    int index = i;
                    EditBox textWidget = WidgetFactory.getTextField(screen, x + 2, y + 30 + i * 14, width - 17, 13, null, () -> "");
                    textWidget.setResponder((str) -> {
                        this.triggerUpdate(update);
                    });
                    textWidget.visible = false;


                    Button btnRemove = new SmallButton(x + width - 13, y + 30 + i * 14, 13, 12, new TextComponent("-"), (btn) -> {
                        FriendlyByteBuf buf = Util.createBuf();
                        buf.writeByte(1);
                        buf.writeInt(index);
                        update.accept(buf);
                    });
                    btnRemove.visible = false;

                    this.lineInput.add(textWidget);
                    this.removeInput.add(btnRemove);
                }

                this.addBtn = new SmallButton(x + 18, y + 16, 13, 12, new TextComponent("+"), (btn) -> {
                    if (this.lines < MAX_LINES) {
                        this.lineInput.get(this.lines).visible = true;
                        this.removeInput.get(this.lines++).visible = true;
                        btn.active = this.lines < MAX_LINES;
                        this.btnRemove.active = this.lines > 0;
                        this.triggerUpdate(update);
                    }
                });
                this.btnRemove = new SmallButton(x + 2, y + 16, 13, 12, new TextComponent("-"), (btn) -> {
                    if (this.lines > 0) {
                        this.lineInput.get(--this.lines).visible = false;
                        this.removeInput.get(this.lines).visible = false;
                        btn.active = this.lines > 0;
                        this.addBtn.active = this.lines < MAX_LINES;
                    }
                });
                this.btnRemove.active = false;

                this.lineInput.forEach(add);
                this.removeInput.forEach(add);
                add.accept(this.addBtn);
                add.accept(this.btnRemove);
            }

            public void triggerUpdate(Consumer<FriendlyByteBuf> update) {
                FriendlyByteBuf buf = Util.createBuf();
                buf.writeByte(0);
                List<String> lines = Lists.newArrayList();
                for (EditBox field : this.lineInput) {
                    if (field.visible) {
                        lines.add(field.getValue());
                    } else {
                        break;
                    }
                }
                buf.writeInt(lines.size());
                lines.forEach((l) -> buf.writeUtf(l, 256));

                update.accept(buf);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {

                if (NBTUtil.hasTagInSubCompound(stack, "display", "Lore", Tag.TAG_LIST)) {
                    ListTag list = stack.getTag().getCompound("display").getList("Lore", Tag.TAG_STRING);

                    for (int i = 0; i < MAX_LINES; i++) {
                        EditBox textWidget = this.lineInput.get(i);
                        Button removeBtn = this.removeInput.get(i);

                        if (i < list.size()) {
                            Component text = Component.Serializer.fromJson(list.getString(i));
                            WidgetUtil.setTextQuietly(textWidget,  text == null ? "" : text.getContents());
                            textWidget.visible = true;
                            removeBtn.visible = true;
                        } else {
                            WidgetUtil.setTextQuietly(textWidget, "");
                            textWidget.visible = false;
                            removeBtn.visible = false;
                        }
                    }

                    this.lines = list.size();

                    this.addBtn.active = this.lines < MAX_LINES;
                    this.btnRemove.active = this.lines > 0;
                } else {
                    for (int i = 0; i < MAX_LINES; i++) {
                        EditBox textWidget = this.lineInput.get(i);
                        Button removeBtn = this.removeInput.get(i);
                        WidgetUtil.setTextQuietly(textWidget, "");
                        textWidget.visible = false;
                        removeBtn.visible = false;
                    }

                    this.lines = 0;
                    this.addBtn.active = this.lines < MAX_LINES;
                    this.btnRemove.active = false;
                }
            }

            @Override
            public void clear(Screen screen) {
                this.lineInput.clear();
                this.removeInput.clear();
                this.lines = 0;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return true; // TODO
            }
        };
    }

    @Override
    public State getFeatureState() {
        return State.ALPHA;
    }
}
