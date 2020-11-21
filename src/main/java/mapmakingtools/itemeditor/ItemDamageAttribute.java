package mapmakingtools.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Strings;

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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

public class ItemDamageAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item.isDamageable();
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            int damage = buffer.readInt();

            if (damage == 0) {
                NBTUtil.removeTag(stack, "Damage", Constants.NBT.TAG_ANY_NUMERIC);
                NBTUtil.removeTagIfEmpty(stack);
            }
            else {
                stack.setDamage(damage);
            }

            return stack;
        case 1:
            CompoundNBT nbt = NBTUtil.getOrCreateTag(stack);

            if (NBTUtil.hasTag(stack, "Unbreakable", Constants.NBT.TAG_BYTE) && nbt.getBoolean("Unbreakable")) {
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

            private TextFieldWidget damageInput;
            private Button unbreakableBtn;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.damageInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.damageInput, stack::getDamage);

                this.damageInput.setMaxStringLength(3);
                this.damageInput.setResponder(BufferFactory.createInteger(0, Util.IS_NULL_OR_EMPTY.or(""::equals), update));
                this.damageInput.setValidator(Util.NUMBER_INPUT_PREDICATE);

                this.unbreakableBtn = new Button(x + width / 2 - 100, y + 40, 200, 20, new TranslationTextComponent(getTranslationKey("button.toggle.unbreakable")), BufferFactory.ping(1, update));

                add.accept(this.damageInput);
                add.accept(this.unbreakableBtn);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (!Strings.isNullOrEmpty(this.damageInput.getText())) {
                    WidgetUtil.setTextQuietly(this.damageInput, String.valueOf(stack.getDamage()));
                }

                //this.unbreakableBtn.active = NBTUtil.hasTag(stack, "Unbreakable", Constants.NBT.TAG_BYTE) && stack.getTag().getBoolean("Unbreakable");
            }

            @Override
            public void clear(Screen screen) {
                this.damageInput = null;
                this.unbreakableBtn = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return newStack.getRepairCost() != oldStack.getRepairCost();
            }
        };
    }

}
