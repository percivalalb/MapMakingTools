package mapmakingtools.itemeditor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

// TODO add ability to edit potion color `CustomPotionColor`
public class PotionAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item == Items.POTION || item == Items.SPLASH_POTION;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:
            int level = buffer.readInt();
            int amount = buffer.readInt();
            for (int i = 0; i < amount; i++) {
                Effect effect = buffer.readRegistryIdUnsafe(ForgeRegistries.POTIONS);
                PotionUtils.appendEffects(stack, Collections.singletonList(new EffectInstance(effect, amount, level)));
            }

            return stack;
        case 1:

            return stack;
        case 2:

            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private ToggleBoxList<Effect> potionList;
            private ToggleBoxList<EffectInstance> currentPotionList;
            private Button addBtn, removeBtn, removeAllBtn;
            private TextFieldWidget lvlInput;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.potionList = new ToggleBoxList<>(x + 2, y + 12, width - 4, (height - 80) / 2, this.potionList);
                this.potionList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.potionList.setValues(ForgeRegistries.POTIONS.getValues(), Effect::getRegistryName, this.potionList);

                this.currentPotionList = new ToggleBoxList<>(x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, this.currentPotionList);
                this.currentPotionList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.currentPotionList.setValues(PotionUtils.getEffectsFromStack(stack), EffectInstance::toString, this.currentPotionList);

                //this.currentEnchantmentList.set
                this.addBtn = new Button(x + 60, y + height / 2 - 23, 50, 20, new TranslationTextComponent(getTranslationKey("button.add")), (btn) -> {
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(0);
                    List<Effect> effects = this.potionList.getGroupManager().getSelected();
                    buf.writeInt(Integer.valueOf(this.lvlInput.getText()));
                    buf.writeInt(effects.size());
                    effects.forEach(ench -> {
                        buf.writeRegistryIdUnsafe(ForgeRegistries.POTIONS, ench);
                    });
                    update.accept(buf);
                });

                this.removeBtn = new Button(x + 60, y + height - 23, 60, 20, new TranslationTextComponent(getTranslationKey("button.remove")), (btn) -> {
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(1);
                    List<EffectInstance> enchamtments = this.currentPotionList.getGroupManager().getSelected();
                    System.out.println(enchamtments);
                    buf.writeInt(enchamtments.size());
//                    enchamtments.forEach(enchDetails -> {
//                        System.out.println(enchDetails.getDisplayString());
//                        buf.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, enchDetails.getEnchantment());
//                        buf.writeInt(enchDetails.getLevel());
//                    });
                    update.accept(buf);
                });

                this.removeAllBtn = new Button(x + 130, y + height - 23, 130, 20, new TranslationTextComponent(getTranslationKey("button.remove.all")), (btn) -> {
                    PacketBuffer buf = Util.createBuf();
                    buf.writeByte(2);
                    update.accept(buf);
                });

                this.lvlInput = WidgetFactory.getTextField(screen, x + 2, y + height / 2 - 20, 50, 14, this.lvlInput, "1"::toString);

                this.lvlInput.setMaxStringLength(3);
                this.lvlInput.setValidator(Util.NUMBER_INPUT_PREDICATE);

                add.accept(this.potionList);
                add.accept(this.currentPotionList);
                add.accept(this.addBtn);
                add.accept(this.removeBtn);
                add.accept(this.removeAllBtn);
                add.accept(this.lvlInput);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.currentPotionList.setValues(PotionUtils.getEffectsFromStack(stack), EffectInstance::toString, this.currentPotionList);
            }

            @Override
            public void clear(Screen screen) {
                this.potionList = null;
                this.currentPotionList = null;
                this.addBtn = null;
                this.removeBtn = null;
                this.removeAllBtn = null;
                this.lvlInput = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return true; // TODO
            }
        };
    }
}
