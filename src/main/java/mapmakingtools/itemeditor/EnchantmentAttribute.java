package mapmakingtools.itemeditor;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnchantmentAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    public String getNBTName() {
        return "Enchantments";
    }

    public void addEnchantment(ItemStack stack, Enchantment ench, int level) {
        stack.enchant(ench, level);
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            int level = buffer.readInt();
            int amount = buffer.readInt();
            for (int i = 0; i < amount; i++) {
                Enchantment ench = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
                this.addEnchantment(stack, ench, level);
            }

            return stack;
        case 1:
            if (NBTUtil.hasTag(stack, getNBTName(), Constants.NBT.TAG_LIST)) {
                ListTag enchantments = stack.getTag().getList(getNBTName(), Constants.NBT.TAG_COMPOUND);
                int amount2 = buffer.readInt();
                for (int i = 0; i < amount2; i++) {
                    EnchantmentDetails details = EnchantmentDetails.readFromBuffer(buffer);

                    for (int j = 0; j < enchantments.size(); j++) {
                        CompoundTag nbt = enchantments.getCompound(j);
                        if (details.equals(nbt)) {
                            enchantments.remove(j);
                            break;
                        }
                    }
                }

                if (enchantments.isEmpty()) {
                    stack.getTag().remove(getNBTName());
                }
                NBTUtil.removeTagIfEmpty(stack);
            }
            return stack;
        case 2:
            if (stack.hasTag()) {
                stack.getTag().remove(getNBTName());
                NBTUtil.removeTagIfEmpty(stack);
            }
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    public List<EnchantmentDetails> getEnchaments(ItemStack stack) {
        List<EnchantmentDetails> enchantments = Lists.newArrayList();
        if (NBTUtil.hasTag(stack, getNBTName(), Constants.NBT.TAG_LIST)) {
            ListTag enchantmentList = stack.getTag().getList(getNBTName(), Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < enchantmentList.size(); ++i) {
                CompoundTag t = enchantmentList.getCompound(i);
                Optional.ofNullable(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(t.getString("id")))).ifPresent((enchantment) -> {
                    enchantments.add(new EnchantmentDetails(enchantment, t.getInt("lvl")));
                });
            }
        }

        return enchantments;
    }

    public static class EnchantmentDetails {
        private Enchantment enchantment;
        private int level;

        public EnchantmentDetails(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        public static EnchantmentDetails readFromBuffer(FriendlyByteBuf buf) {
            Enchantment ench = buf.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
            int level = buf.readInt();

            return new EnchantmentDetails(ench, level);
        }

        public boolean equals(CompoundTag nbt) {
            if (nbt.getInt("lvl") != this.level) {
                return false;
            }

            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(nbt.getString("id")));
            return Objects.equal(ench, this.enchantment);
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public int getLevel() {
            return this.level;
        }

        public String getDisplayString() {
            return this.enchantment.getFullname(this.level).getString();
        }
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private ToggleBoxList<Enchantment> enchantmentList;
            private ToggleBoxList<EnchantmentDetails> currentEnchantmentList;
            private Button addBtn, removeBtn, removeAllBtn;
            private EditBox lvlInput;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.enchantmentList = new ToggleBoxList<>(x + 2, y + 12, width - 4, (height - 80) / 2, this.enchantmentList);
                this.enchantmentList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.enchantmentList.setValues(ForgeRegistries.ENCHANTMENTS.getValues(), Enchantment::getRegistryName, this.enchantmentList);

                this.currentEnchantmentList = new ToggleBoxList<>(x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, this.currentEnchantmentList);
                this.currentEnchantmentList.setSelectionGroupManager(ToggleBoxGroup.builder(EnchantmentDetails.class).min(0).max(Integer.MAX_VALUE).listen((selection) -> { this.removeBtn.active = !selection.isEmpty(); }).build());
                this.currentEnchantmentList.setValues(getEnchaments(stack), EnchantmentDetails::getDisplayString, this.currentEnchantmentList);

                //this.currentEnchantmentList.set
                this.addBtn = new Button(x + 60, y + height / 2 - 23, 50, 20, new TranslatableComponent(getTranslationKey("button.add")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    List<Enchantment> enchamtments = this.enchantmentList.getGroupManager().getSelected();
                    buf.writeInt(Integer.valueOf(this.lvlInput.getValue()));
                    buf.writeInt(enchamtments.size());
                    enchamtments.forEach(ench -> {
                        buf.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, ench);
                    });
                    update.accept(buf);
                });

                this.removeBtn = new Button(x + 60, y + height - 23, 60, 20, new TranslatableComponent(getTranslationKey("button.remove")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(1);
                    List<EnchantmentDetails> enchamtments = this.currentEnchantmentList.getGroupManager().getSelected();
                    System.out.println(enchamtments);
                    buf.writeInt(enchamtments.size());
                    enchamtments.forEach(enchDetails -> {
                        System.out.println(enchDetails.getDisplayString());
                        buf.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, enchDetails.getEnchantment());
                        buf.writeInt(enchDetails.getLevel());
                    });
                    update.accept(buf);
                });

                this.removeAllBtn = new Button(x + 130, y + height - 23, 130, 20, new TranslatableComponent(getTranslationKey("button.remove.all")), BufferFactory.ping(2, update));

                this.lvlInput = WidgetFactory.getTextField(screen, x + 2, y + height / 2 - 20, 50, 14, this.lvlInput, "1"::toString);
                this.lvlInput.setMaxLength(3);
                this.lvlInput.setFilter(Util.NUMBER_INPUT_PREDICATE);


                add.accept(this.enchantmentList);
                add.accept(this.currentEnchantmentList);
                add.accept(this.addBtn);
                add.accept(this.removeBtn);
                add.accept(this.removeAllBtn);
                add.accept(this.lvlInput);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.currentEnchantmentList.setValues(getEnchaments(stack), EnchantmentDetails::getDisplayString, this.currentEnchantmentList);
                this.removeBtn.active = false;
                this.removeAllBtn.active = this.currentEnchantmentList.getNoElements() > 0;
            }

            @Override
            public void clear(Screen screen) {
                this.enchantmentList = null;
                this.currentEnchantmentList = null;
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
