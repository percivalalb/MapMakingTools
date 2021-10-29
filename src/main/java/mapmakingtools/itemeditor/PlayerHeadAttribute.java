package mapmakingtools.itemeditor;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlayerHeadAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item == Items.PLAYER_HEAD;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            this.setPlayerName(stack, buffer.readUtf(128));
            return stack;
        case 1:
            this.setPlayerName(stack, null);
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Nullable
    public String getSkullName(ItemStack stack) {
        if (NBTUtil.hasTag(stack, "SkullOwner", Constants.NBT.TAG_STRING)) {
            return stack.getTag().getString("SkullOwner");
        } else if (NBTUtil.hasTag(stack, "SkullOwner", Constants.NBT.TAG_COMPOUND)) {
            CompoundTag skullNBT = stack.getTag().getCompound("SkullOwner");
            if (skullNBT.contains("Name", Constants.NBT.TAG_STRING)) {
                return skullNBT.getString("Name");
            }
        }
        return null;
    }

    public void setPlayerName(ItemStack stack, @Nullable String name) {
        if (!Strings.isNullOrEmpty(name)) {
            NBTUtil.getOrCreateTag(stack).putString("SkullOwner", name);
        } else {
            if (stack.hasTag()) {
                stack.getTag().remove("SkullOwner");
                NBTUtil.removeTagIfEmpty(stack);
            }
        }
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private EditBox nameInput;
            private Button nameRemoval;
            private Optional<Boolean> nameExists = Optional.empty();
            private long triggerAfter = -1;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.nameInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.nameInput, () -> getSkullName(stack));
                this.nameInput.setMaxLength(128);
                this.nameInput.setResponder(str -> {
                    this.triggerAfter = net.minecraft.Util.getMillis() + 750L;
                });

                this.nameRemoval = new Button(x + width / 2 - 100, y + 40, 200, 20, new TranslatableComponent(getTranslationKey("button.remove")), (btn) -> {
                    WidgetUtil.setTextQuietly(this.nameInput, "");
                    btn.active = false;
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(1);
                    update.accept(buf);
                });

                add.accept(this.nameInput);
                add.accept(this.nameRemoval);
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
                this.nameExists.ifPresent(b -> {
                    String text = b ? "text.player.exists" : "text.player.exists.not";
                    int colour = b ? 65025 : 16581375;
                    screen.getMinecraft().font.draw(stackIn, new TranslatableComponent(getTranslationKey(text)), x + 2, y + 30, colour);
                });
            }

            @Override
            public void tick(Screen screen, long systemTimeMillis, Consumer<FriendlyByteBuf> update) {
                if (this.triggerAfter != -1 && systemTimeMillis >= this.triggerAfter) {
                    this.nameRemoval.active = true;
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    buf.writeUtf(this.nameInput.getValue(), 128);
                    update.accept(buf);
                    this.triggerAfter = -1;
                }
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                String skullName = getSkullName(stack);
                this.nameExists = Optional.of(NBTUtil.hasTagInSubCompound(stack, "SkullOwner", "Id", Constants.NBT.TAG_STRING));
                WidgetUtil.setTextQuietly(this.nameInput, Objects.toString(skullName, ""));
                this.nameRemoval.active = skullName != null;
            }

            @Override
            public void clear(Screen screen) {
                this.nameInput = null;
                this.nameRemoval = null;
                this.nameExists = Optional.empty();
                this.triggerAfter = -1;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return !Objects.equals(getSkullName(newStack), getSkullName(oldStack));
            }

        };
    }

}
