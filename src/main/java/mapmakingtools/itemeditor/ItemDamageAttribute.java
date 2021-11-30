package mapmakingtools.itemeditor;

import com.google.common.base.Strings;
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
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemDamageAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item.canBeDepleted();
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            int damage = buffer.readInt();

            if (damage == 0) {
                NBTUtil.removeTag(stack, "Damage", Tag.TAG_ANY_NUMERIC);
                NBTUtil.removeTagIfEmpty(stack);
            }
            else {
                stack.setDamageValue(damage);
            }

            return stack;
        case 1:
            CompoundTag nbt = NBTUtil.getOrCreateTag(stack);

            if (NBTUtil.hasTag(stack, "Unbreakable", Tag.TAG_BYTE) && nbt.getBoolean("Unbreakable")) {
                nbt.remove("Unbreakable");
            } else {
                nbt.putBoolean("Unbreakable", true);
            }

            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private EditBox damageInput;
            private Button unbreakableBtn;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
                this.damageInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.damageInput, () -> stack.get().getDamageValue());

                this.damageInput.setMaxLength(3);
                this.damageInput.setResponder(BufferFactory.createInteger(0, Util.IS_NULL_OR_EMPTY.or(""::equals), update));
                this.damageInput.setFilter(Util.NUMBER_INPUT_PREDICATE);

                this.unbreakableBtn = new Button(x + width / 2 - 100, y + 40, 200, 20, new TranslatableComponent(getTranslationKey("button.toggle.unbreakable")), BufferFactory.ping(1, update));

                add.accept(this.damageInput);
                add.accept(this.unbreakableBtn);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (!Strings.isNullOrEmpty(this.damageInput.getValue())) {
                    WidgetUtil.setTextQuietly(this.damageInput, String.valueOf(stack.getDamageValue()));
                }

                //this.unbreakableBtn.active = NBTUtil.hasTag(stack, "Unbreakable", Tag.TAG_BYTE) && stack.getTag().getBoolean("Unbreakable");
            }

            @Override
            public void clear(Screen screen) {
                this.damageInput = null;
                this.unbreakableBtn = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return newStack.getBaseRepairCost() != oldStack.getBaseRepairCost();
            }
        };
    }

}
