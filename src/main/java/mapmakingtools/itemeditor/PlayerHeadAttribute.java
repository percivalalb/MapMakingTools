package mapmakingtools.itemeditor;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

public class PlayerHeadAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item == Items.PLAYER_HEAD;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            this.setPlayerName(stack, buffer.readString(128));
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
            CompoundNBT skullNBT = stack.getTag().getCompound("SkullOwner");
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

            private TextFieldWidget nameInput;
            private Button nameRemoval;
            private Optional<Boolean> nameExists = Optional.empty();
            private long triggerAfter = -1;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.nameInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.nameInput, () -> getSkullName(stack));
                this.nameInput.setMaxStringLength(128);
                this.nameInput.setResponder(str -> {
                    this.triggerAfter = net.minecraft.util.Util.milliTime() + 750L;
                });

                this.nameRemoval = new Button(x + width / 2 - 100, y + 40, 200, 20, new TranslationTextComponent(getTranslationKey("button.remove")), (btn) -> {
                    WidgetUtil.setTextQuietly(this.nameInput, "");
                    btn.active = false;
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(1);
                    update.accept(buf);
                });

                add.accept(this.nameInput);
                add.accept(this.nameRemoval);
            }

            @Override
            public void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {
                this.nameExists.ifPresent(b -> {
                    String text = b ? "text.player.exists" : "text.player.exists.not";
                    int colour = b ? 65025 : 16581375;
                    screen.getMinecraft().fontRenderer.func_243248_b(stackIn, new TranslationTextComponent(getTranslationKey(text)), x + 2, y + 30, colour);
                });
            }

            @Override
            public void tick(Screen screen, long systemTimeMillis, Consumer<PacketBuffer> update) {
                if (this.triggerAfter != -1 && systemTimeMillis >= this.triggerAfter) {
                    this.nameRemoval.active = true;
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(0);
                    buf.writeString(this.nameInput.getText(), 128);
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
