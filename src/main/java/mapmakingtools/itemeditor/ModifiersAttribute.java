package mapmakingtools.itemeditor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.util.State;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.SmallToggleButton;
import mapmakingtools.client.screen.widget.ToggleButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModifiersAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer, Player player) {
        switch(buffer.readByte()) {
        case 0:

            this.convertModifiersToNBT(stack, player);
            Modifier modifier = MODIFIERS[buffer.readInt()];
            double value = buffer.readDouble();
            AttributeModifier.Operation op = AttributeModifier.Operation.fromValue(buffer.readInt());
            EquipmentSlot equipmentType = EquipmentSlot.byName(buffer.readUtf(64));

            value /= op != AttributeModifier.Operation.ADDITION ? 100 : 1;

            this.removeSimilarModifier(stack, modifier, op, equipmentType);
            stack.addAttributeModifier(modifier.attribute.get(), modifier.getEdited(value, op), equipmentType);
            return stack;
        case 1:
            // Ensure the modifiers have been converted to NBT otherwise we can't
            // actually edit the modifiers list
            this.convertModifiersToNBT(stack, player);

            Modifier modifier2 = MODIFIERS[buffer.readInt()];
            AttributeModifier.Operation op2 = AttributeModifier.Operation.fromValue(buffer.readInt());
            EquipmentSlot equipmentType2 = EquipmentSlot.byName(buffer.readUtf(64));

            this.removeSimilarModifier(stack, modifier2, op2, equipmentType2);
            return stack;
        case 2:
            this.convertModifiersToNBT(stack, player);
            return stack;
        case 3:
            NBTUtil.removeTag(stack, "AttributeModifiers", Tag.TAG_LIST);
            NBTUtil.removeTagIfEmpty(stack);
            return stack;
        case 4:
            stack.getOrCreateTag().put("AttributeModifiers", new ListTag());
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    public void removeSimilarModifier(ItemStack stack,  Modifier modifier, AttributeModifier.Operation op, EquipmentSlot slotType) {
        // Removes modifier with same name, slot and op
        if (NBTUtil.hasTag(stack, "AttributeModifiers", Tag.TAG_LIST)) {
            ListTag nbttaglist = stack.getTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);

            for (int k = 0; k < nbttaglist.size(); k++) {
                CompoundTag compound = nbttaglist.getCompound(k);
                Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(compound.getString("AttributeName")));
                boolean correctOp = compound.getInt("Operation") == op.toValue();
                boolean correctSlot = !compound.contains("Slot", Tag.TAG_STRING)
                        || compound.getString("Slot").equals(slotType.getName());
                boolean correctUUID = modifier.uuid == null || Objects.equals(modifier.uuid, compound.getUUID("UUID"));

                if (attr != null && attr.delegate.equals(modifier.attribute.get().delegate) && correctSlot && correctOp && correctUUID) {
                    nbttaglist.remove(k);
                    break; // Only remove one modifier
                }
            }
        }
    }

    public void convertModifiersToNBT(ItemStack stack, Player player) {
        if (!NBTUtil.hasTag(stack, "AttributeModifiers", Tag.TAG_LIST)) {

            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                Multimap<Attribute, AttributeModifier> builtIn = stack.getAttributeModifiers(equipmentSlot);

                 for (Entry<Attribute, AttributeModifier> entry : builtIn.entries()) {

                     AttributeModifier modifier = entry.getValue();

                     // The damage a player can deal is not just based
                     if (player != null) {
                         double bonus = 0D;
                         if (entry.getValue().getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                             bonus += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                             bonus += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                         } else if (entry.getValue().getId() == Item.BASE_ATTACK_SPEED_UUID) {
                             bonus += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                         }

                         modifier = new AttributeModifier(modifier.getId(), modifier.getName(), modifier.getAmount() + bonus, modifier.getOperation());
                     }

                     stack.addAttributeModifier(entry.getKey(), modifier, equipmentSlot);
                }
            }
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private List<EditBox> valueInputList = Lists.newArrayList();
            private List<ToggleButton<AttributeModifier.Operation>> opBtnList = Lists.newArrayList();
            private List<Button> addBtnList = Lists.newArrayList();
            private List<Button> removeBtnList = Lists.newArrayList();
            private Button convertInternalBtn;
            private Button removeModifiersNBT;
            private Button removeModifiers;
            private ToggleButton<EquipmentSlot> btn_slot;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
                Multimap<Attribute, AttributeModifier> modifiers = stack.get().getAttributeModifiers(EquipmentSlot.CHEST);

                for (int i = 0; i < MODIFIERS.length; i++) {
                    final int index = i;

                    int inputSize = Mth.clamp(width - 200, 20, 100);

                    ToggleButton<AttributeModifier.Operation> opBtn = new SmallToggleButton<>(x + 127 + inputSize, y + 38 + i * 17, 18, 16, AttributeModifier.Operation.values(), this::getOpString, null, (btn) -> {
                        this.populateFrom(screen, stack.get());
                    }, (_button, matrixStack, mouseX, mouseY) -> {
                        ToggleButton<AttributeModifier.Operation> button = (ToggleButton<AttributeModifier.Operation>) _button;
                        if (button.active) {
                            screen.renderTooltip(matrixStack, screen.getMinecraft().font.split(new TranslatableComponent("item_editor.mapmakingtools.modifiers.button.op." + button.getValue().name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.ITALIC), Math.max(screen.width / 2 - 43, 170)), mouseX, mouseY);
                        }
                    });

                    EditBox inputWidget = WidgetFactory.getTextField(screen, x + 122, y + 39 + i * 17, inputSize, 14, null, () -> "");
                    inputWidget.setFilter(Util.NUMBER_INPUT_PREDICATE);


                    Button addBtn = new SmallButton(x + 155 + inputSize, y + 38 + i * 17, 16, 16, new TextComponent("+"), (btn) -> {
                        if (Strings.isNullOrEmpty(inputWidget.getValue()) || "-".equals(inputWidget.getValue())) {
                            return;
                        }

                        FriendlyByteBuf buf = Util.createBuf();
                        buf.writeByte(0);
                        buf.writeInt(index);
                        buf.writeDouble(Double.valueOf(inputWidget.getValue()));
                        buf.writeInt(opBtn.getValue().toValue());
                        buf.writeUtf(this.btn_slot.getValue().getName(), 64);
                        update.accept(buf);
                    }, (button, matrixStack, mouseX, mouseY) -> {
                        if (button.active) {
                            screen.renderTooltip(matrixStack, new TranslatableComponent("item_editor.mapmakingtools.modifiers.button." + (button.getMessage().getString().equals("#") ? "modify" : "add")), mouseX, mouseY);
                        }
                    });

                    Button removeBtn = new SmallButton(x + 171 + inputSize, y + 38 + i * 17, 16, 16, new TextComponent("-"), (btn) -> {
                        FriendlyByteBuf buf = Util.createBuf();
                        buf.writeByte(1);
                        buf.writeInt(index);
                        buf.writeInt(opBtn.getValue().toValue());
                        buf.writeUtf(this.btn_slot.getValue().getName(), 64);
                        update.accept(buf);
                    }, (button, matrixStack, mouseX, mouseY) -> {
                        if (button.active) {
                            screen.renderTooltip(matrixStack, new TranslatableComponent("item_editor.mapmakingtools.modifiers.button.remove"), mouseX, mouseY);
                        }
                    });

                    this.opBtnList.add(opBtn);
                    this.valueInputList.add(inputWidget);
                    this.addBtnList.add(addBtn);
                    this.removeBtnList.add(removeBtn);
                }

                this.btn_slot = new SmallToggleButton<>(x + 2, y + 16, 100, 20, EquipmentSlot.values(), (type) -> new TranslatableComponent("item.modifiers." + type.getName()), this.btn_slot, (btn) -> {
                    this.populateFrom(screen, stack.get());
                });

                this.convertInternalBtn = new Button(x + 110, y + 16, 130, 20, new TranslatableComponent(getTranslationKey("button.convert_to_tag")), BufferFactory.ping(2, update));

                this.removeModifiersNBT = new Button(x + 10, y + height - 23, 130, 20, new TranslatableComponent(getTranslationKey("button.remove.tag")), BufferFactory.ping(3, update));
                this.removeModifiers = new Button(x + 145, y + height - 23, 130, 20, new TranslatableComponent(getTranslationKey("button.remove.all")), BufferFactory.ping(4, update));

                this.valueInputList.forEach(add);
                this.opBtnList.forEach(add);
                this.addBtnList.forEach(add);
                this.removeBtnList.forEach(add);
                add.accept(this.btn_slot);
                add.accept(this.convertInternalBtn);
                add.accept(this.removeModifiers);
                add.accept(this.removeModifiersNBT);
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
                Font font = screen.getMinecraft().font;
                //font.drawString(stackIn, "OP", x + 130 + MathHelper.clamp(width - 200, 40, 100), y + 25, 16777120);
                for (int i = 0; i < MODIFIERS.length; i++) {
                    Attribute attr = MODIFIERS[i].attribute.get();
                    String translationKey = "attribute.name." + attr.getRegistryName().getNamespace() + '.' + attr.getRegistryName().getPath() + (MODIFIERS[i].modifierId != null ? '.' + MODIFIERS[i].modifierId : "");
                    font.draw(stackIn, I18n.exists(translationKey) ? new TranslatableComponent(translationKey) : new TextComponent(MODIFIERS[i].attribute.get().getDescriptionId()), x + 6, y + 42 + i * 17, 16777120);
                }
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(this.btn_slot.getValue());

                MODIFIER: for (int i = 0; i < MODIFIERS.length; i++) {
                    Modifier modifier = MODIFIERS[i];

                    for (Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                        Attribute key = entry.getKey();
                        AttributeModifier attributemodifier = entry.getValue();

                        boolean correctOp = attributemodifier.getOperation() == this.opBtnList.get(i).getValue();
                        boolean correctUUID = modifier.uuid == null || Objects.equals(modifier.uuid, attributemodifier.getId());

                        if (key.delegate.equals(modifier.attribute.get().delegate) && correctOp && correctUUID) {
                            this.addBtnList.get(i).setMessage(new TextComponent("#"));

                            double amount = attributemodifier.getAmount();

                            if (attributemodifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                                amount += screen.getMinecraft().player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                                amount += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                            } else if (attributemodifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                                amount += screen.getMinecraft().player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                            }

                            int scale = attributemodifier.getOperation() != AttributeModifier.Operation.ADDITION ? 100 : key.equals(Attributes.KNOCKBACK_RESISTANCE) ? 10 : 1;
                            amount *= scale;

                            this.valueInputList.get(i).setValue(Util.tryToWriteAsInt(amount));
                            this.opBtnList.get(i).setValue(attributemodifier.getOperation());
                            this.removeBtnList.get(i).visible = true;
                            continue MODIFIER;
                        }
                    }

                    // Only run if no modifier is found
                    this.addBtnList.get(i).setMessage(new TextComponent("+"));

                    this.valueInputList.get(i).setValue("");
                    //this.opBtnList.get(i).setValue(AttributeModifier.Operation.ADDITION);
                    this.removeBtnList.get(i).visible = false;
                }

                this.removeModifiers.active = Arrays.stream(EquipmentSlot.values()).anyMatch((slotType) -> !stack.getItem().getAttributeModifiers(slotType, stack).isEmpty());
                this.removeModifiersNBT.active = NBTUtil.hasTag(stack, "AttributeModifiers", Tag.TAG_LIST);
                this.convertInternalBtn.active = !this.removeModifiersNBT.active && this.removeModifiers.active;
            }

            @Override
            public void clear(Screen screen) {
                this.valueInputList.clear();
                this.opBtnList.clear();
                this.addBtnList.clear();
                this.removeBtnList.clear();
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return true; // TODO
            }

            public Component getOpString(AttributeModifier.Operation op) {
                switch(op) {
                case ADDITION:
                    return new TextComponent("+|");
                case MULTIPLY_BASE:
                    return new TextComponent("+%");
                case MULTIPLY_TOTAL:
                    return new TextComponent("x%");
                default:
                    return new TextComponent("??");
                }
            }
        };
    }

    public static AttributeModifier.Operation ADD_OPERATION = AttributeModifier.Operation.ADDITION;
    public static AttributeModifier.Operation MULT_PERCENTAGE_OPERATION = AttributeModifier.Operation.MULTIPLY_BASE;
    public static AttributeModifier.Operation ADD_PERCENTAGE_OPERATION = AttributeModifier.Operation.MULTIPLY_TOTAL;

    //     Item.ATTACK_DAMAGE_MODIFIER
    // Until hardcoded == on UUID are removed just use null
    private Modifier ATTACK_DAMAGE = new Modifier(null, Attributes.ATTACK_DAMAGE.delegate, "Weapon modifier", "weapon");
    private Modifier ATTACK_SPEED = new Modifier(null, Attributes.ATTACK_SPEED.delegate, "Weapon modifier", "weapon");
    private Modifier ATTACK_KNOCKBACK = new Modifier(Attributes.ATTACK_KNOCKBACK.delegate, "Weapon modifier");
    private Modifier KNOCKBACK_RESISTANCE = new Modifier(Attributes.KNOCKBACK_RESISTANCE.delegate, "Knockback Resistance");
    private Modifier MAX_HEALTH = new Modifier(Attributes.MAX_HEALTH.delegate, "Max Health");
    private Modifier MOVEMENT_SPEED = new Modifier(Attributes.MOVEMENT_SPEED.delegate, "Movement Speed"); // MULT_PERCENTAGE_OPERATION
    private Modifier SPRINTING_SPEED_BOOST = new Modifier(LivingEntity.SPEED_MODIFIER_SPRINTING_UUID, Attributes.MOVEMENT_SPEED.delegate, "Sprinting speed boost", "sprinting"); //MULTIPLY_TOTAL
    private Modifier FLYING_SPEED = new Modifier(Attributes.FLYING_SPEED.delegate, "Flying Speed"); // MULT_PERCENTAGE_OPERATION
    private Modifier FOLLOW_RANGE = new Modifier(Attributes.FOLLOW_RANGE.delegate, "Follow Range"); // MULT_PERCENTAGE_OPERATION
    private Modifier ARMOR = new Modifier(Attributes.ARMOR.delegate, "Armor modifier");
    private Modifier ARMOR_TOUGHNESS = new Modifier(Attributes.ARMOR_TOUGHNESS.delegate, "Armor toughness");
    private Modifier SPAWN_REINFORCEMENTS = new Modifier(Attributes.SPAWN_REINFORCEMENTS_CHANCE.delegate, "Spawn Reinforcements Chance");
    private Modifier BABY_SPEED_BOOST = new Modifier(Zombie.SPEED_MODIFIER_BABY_UUID, Attributes.MOVEMENT_SPEED.delegate, "Baby speed boost", "zombie.baby");
    private Modifier HORSE_JUMP_STRENGTH = new Modifier(Attributes.JUMP_STRENGTH.delegate, "Jump Strength");
    private Modifier HORSE_ARMOR = new Modifier(Horse.ARMOR_MODIFIER_UUID, Attributes.ARMOR.delegate, "Horse armor bonus", "horse.bonus");

    // Potion Luck Modifier
    private Modifier LUCK = new Modifier(UUID.fromString("03C3C89D-7037-4B42-869F-B146BCB64D2E"), Attributes.LUCK.delegate, MobEffects.LUCK.getDescriptionId(), null);
    private Modifier UNLUCK = new Modifier(UUID.fromString("CC5AF142-2BD2-4215-B636-2605AED11727"), Attributes.LUCK.delegate, MobEffects.UNLUCK.getDescriptionId(), "un");

    // Forge Modifiers
    private Modifier SLOW_FALLING = new Modifier(UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA"), ForgeMod.ENTITY_GRAVITY, "Slow falling acceleration reduction", "slow.falling");
    private Modifier ENITTY_GRAVITY = new Modifier(ForgeMod.ENTITY_GRAVITY, "Gravity");
    private Modifier NAMETAG_DISTANCE = new Modifier(ForgeMod.NAMETAG_DISTANCE, "Name Tag");
    private Modifier SWIM_SPEED = new Modifier(ForgeMod.SWIM_SPEED, "Swim Speed");

    private final Modifier[] MODIFIERS = new Modifier[] {ATTACK_DAMAGE, ATTACK_SPEED, ATTACK_KNOCKBACK, KNOCKBACK_RESISTANCE, MAX_HEALTH, MOVEMENT_SPEED, SPRINTING_SPEED_BOOST, FLYING_SPEED, FOLLOW_RANGE, ARMOR, ARMOR_TOUGHNESS, LUCK, UNLUCK, SLOW_FALLING, ENITTY_GRAVITY, NAMETAG_DISTANCE, SWIM_SPEED, HORSE_ARMOR, HORSE_JUMP_STRENGTH, SPAWN_REINFORCEMENTS, BABY_SPEED_BOOST};

    public static class Modifier {

        public String attributeName;
        public Supplier<Attribute> attribute;
        @Nullable
        public UUID uuid;
        public String modifierId;

        public Modifier(Supplier<Attribute> attribute, String name) {
            this(null, attribute, name, null);
        }

        public Modifier(UUID uuid, Supplier<Attribute> attribute, String name, String modifierId) {
            this.uuid = uuid;
            this.attribute = attribute;
            this.attributeName = name;
            this.modifierId = modifierId;
        }

        public AttributeModifier getEdited(double amountIn, AttributeModifier.Operation operationIn) {
            if (this.uuid == null) {
                return new AttributeModifier(this.attributeName, amountIn, operationIn);
            } else {
                return new AttributeModifier(this.uuid, this.attributeName, amountIn, operationIn);
            }
        }
    }

    @Override
    public State getFeatureState() {
        return State.BETA;
    }
}
