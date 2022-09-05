package mapmakingtools.itemeditor;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.client.screen.widget.ToggleBoxRegistryList;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO add ability to edit potion color `CustomPotionColor`
public class PotionAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item == Items.POTION || item == Items.SPLASH_POTION;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            int level = buffer.readInt();
            int amount = buffer.readInt();
            for (int i = 0; i < amount; i++) {
                MobEffect effect = buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
                PotionUtils.setCustomEffects(stack, Collections.singletonList(new MobEffectInstance(effect, amount, level)));
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

            private ToggleBoxRegistryList<MobEffect> potionList;
            private ToggleBoxList<MobEffectInstance> currentPotionList;
            private Button addBtn, removeBtn, removeAllBtn;
            private EditBox lvlInput;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.potionList = new ToggleBoxRegistryList<>(x + 2, y + 12, width - 4, (height - 80) / 2, this.potionList);
                this.potionList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.potionList.setValues(ForgeRegistries.MOB_EFFECTS, this.potionList);

                this.currentPotionList = new ToggleBoxList<>(x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, this.currentPotionList);
                this.currentPotionList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.currentPotionList.setValues(PotionUtils.getMobEffects(stack), MobEffectInstance::toString, this.currentPotionList);

                //this.currentEnchantmentList.set
                this.addBtn = new Button(x + 60, y + height / 2 - 23, 50, 20, Component.translatable(getTranslationKey("button.add")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    List<Map.Entry<ResourceKey<MobEffect>, MobEffect>> effects = this.potionList.getGroupManager().getSelected();
                    buf.writeInt(Integer.valueOf(this.lvlInput.getValue()));
                    buf.writeInt(effects.size());
                    effects.forEach(ench -> {
                        buf.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, ench.getValue());
                    });
                    update.accept(buf);
                });

                this.removeBtn = new Button(x + 60, y + height - 23, 60, 20, Component.translatable(getTranslationKey("button.remove")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(1);
                    List<MobEffectInstance> enchamtments = this.currentPotionList.getGroupManager().getSelected();
                    System.out.println(enchamtments);
                    buf.writeInt(enchamtments.size());
//                    enchamtments.forEach(enchDetails -> {
//                        System.out.println(enchDetails.getDisplayString());
//                        buf.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, enchDetails.getEnchantment());
//                        buf.writeInt(enchDetails.getLevel());
//                    });
                    update.accept(buf);
                });

                this.removeAllBtn = new Button(x + 130, y + height - 23, 130, 20, Component.translatable(getTranslationKey("button.remove.all")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(2);
                    update.accept(buf);
                });

                this.lvlInput = WidgetFactory.getTextField(screen, x + 2, y + height / 2 - 20, 50, 14, this.lvlInput, "1"::toString);

                this.lvlInput.setMaxLength(3);
                this.lvlInput.setFilter(Util.NUMBER_INPUT_PREDICATE);

                add.accept(this.potionList);
                add.accept(this.currentPotionList);
                add.accept(this.addBtn);
                add.accept(this.removeBtn);
                add.accept(this.removeAllBtn);
                add.accept(this.lvlInput);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.currentPotionList.setValues(PotionUtils.getMobEffects(stack), MobEffectInstance::toString, this.currentPotionList);
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
