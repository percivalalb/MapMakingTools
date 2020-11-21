package mapmakingtools.itemeditor;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.SmallToggleButton;
import mapmakingtools.client.screen.widget.ToggleButton;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;

public class ModifiersAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
        case 0:

            this.convertModifersToNBT(stack);
            Modifier modifier = MODIFIERS[buffer.readInt()];
            double value = buffer.readInt();
            AttributeModifier.Operation op = AttributeModifier.Operation.byId(buffer.readInt());
            EquipmentSlotType equipmentType = EquipmentSlotType.fromString(buffer.readString(64));

            value /= op != AttributeModifier.Operation.ADDITION ? 100 : 1;

            this.removeSimilarModifier(stack, modifier, op, equipmentType);
            stack.addAttributeModifier(modifier.attribute.get(), modifier.getEdited(value, op), equipmentType);
            return stack;
        case 1:
            Modifier modifier2 = MODIFIERS[buffer.readInt()];
            AttributeModifier.Operation op2 = AttributeModifier.Operation.byId(buffer.readInt());
            EquipmentSlotType equipmentType2 = EquipmentSlotType.fromString(buffer.readString(64));

            this.removeSimilarModifier(stack, modifier2, op2, equipmentType2);
            return stack;
        case 2:
            this.convertModifersToNBT(stack);
            return stack;
        case 3:
            NBTUtil.removeTag(stack, "AttributeModifiers", Constants.NBT.TAG_LIST);
            NBTUtil.removeTagIfEmpty(stack);
            return stack;
        case 4:
            stack.getOrCreateTag().put("AttributeModifiers", new ListNBT());
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    public void removeSimilarModifier(ItemStack stack,  Modifier modifier, AttributeModifier.Operation op, EquipmentSlotType slotType) {
        // Removes modifier with same name, slot and op
        if (NBTUtil.hasTag(stack, "AttributeModifiers", Constants.NBT.TAG_LIST)) {
            ListNBT nbttaglist = stack.getTag().getList("AttributeModifiers", Constants.NBT.TAG_COMPOUND);

            for (int k = 0; k < nbttaglist.size(); k++) {
                CompoundNBT compound = nbttaglist.getCompound(k);
                String attributeName = compound.getString("AttributeName");
                boolean correctOp = compound.getInt("Operation") == op.getId();
                boolean correctSlot = !compound.contains("Slot", Constants.NBT.TAG_STRING)
                        || compound.getString("Slot").equals(slotType.getName());
                boolean correctUUID = modifier.uuid == null || modifier.uuid.equals(compound.getUniqueId("UUID"));

                if (attributeName.equals(modifier.attributeName) && correctSlot && correctOp && correctUUID) {
                    nbttaglist.remove(k);
                    break;
                }
            }
        }
    }

    public void convertModifersToNBT(ItemStack stack) {
        if (!NBTUtil.hasTag(stack, "AttributeModifiers", Constants.NBT.TAG_LIST)) {

            for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
                Multimap<Attribute, AttributeModifier> builtIn = stack.getAttributeModifiers(equipmentSlot);

                 for (Entry<Attribute, AttributeModifier> entry : builtIn.entries()) {
                     stack.addAttributeModifier(entry.getKey(), entry.getValue(), equipmentSlot);
                }
            }
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private List<TextFieldWidget> valueInputList = Lists.newArrayList();
            private List<ToggleButton<AttributeModifier.Operation>> opBtnList = Lists.newArrayList();
            private List<Button> addBtnList = Lists.newArrayList();
            private List<Button> removeBtnList = Lists.newArrayList();
            private Button convertInternalBtn;
            private Button removeModifiersNBT;
            private Button removeModifiers;
            private ToggleButton<EquipmentSlotType> btn_slot;

            @Override
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final Supplier<ItemStack> stack, int x, int y, int width, int height) {
                Multimap<Attribute, AttributeModifier> modifiers = stack.get().getAttributeModifiers(EquipmentSlotType.CHEST);

                for (int i = 0; i < MODIFIERS.length; i++) {
                    int index = i;

                    int inputSize = MathHelper.clamp(width - 200, 20, 100);

                    ToggleButton<AttributeModifier.Operation> opBtn = new SmallToggleButton<>(x + 127 + inputSize, y + 38 + i * 17, 18, 16, AttributeModifier.Operation.values(), this::getOpString, null, (btn) -> {
                        this.populateFrom(screen, stack.get());
                    });

                    TextFieldWidget inputWidget = WidgetFactory.getTextField(screen, x + 122, y + 39 + i * 17, inputSize, 14, null, () -> "");
                    inputWidget.setValidator(Util.NUMBER_INPUT_PREDICATE);


                    Button addBtn = new SmallButton(x + 155 + inputSize, y + 38 + i * 17, 16, 16, new StringTextComponent("+"), (btn) -> {
                        if (Strings.isNullOrEmpty(inputWidget.getText()) || "-".equals(inputWidget.getText())) {
                            return;
                        }

                        PacketBuffer buf = Util.createBuf();
                        buf.writeByte(0);
                        buf.writeInt(index);
                        buf.writeInt(Integer.valueOf(inputWidget.getText()));
                        buf.writeInt(opBtn.getValue().getId());
                        buf.writeString(this.btn_slot.getValue().getName(), 64);
                        update.accept(buf);
                    });

                    Button removeBtn = new SmallButton(x + 171 + inputSize, y + 38 + i * 17, 16, 16, new StringTextComponent("-"), (btn) -> {
                        PacketBuffer buf = Util.createBuf();
                        buf.writeByte(1);
                        buf.writeInt(index);
                        buf.writeInt(opBtn.getValue().getId());
                        buf.writeString(this.btn_slot.getValue().getName(), 64);
                        update.accept(buf);
                    });

                    this.opBtnList.add(opBtn);
                    this.valueInputList.add(inputWidget);
                    this.addBtnList.add(addBtn);
                    this.removeBtnList.add(removeBtn);
                }

                this.btn_slot = new SmallToggleButton<>(x + 2, y + 16, 100, 20, EquipmentSlotType.values(), (type) -> new TranslationTextComponent("item.modifiers." + type.getName()), this.btn_slot, (btn) -> {
                    this.populateFrom(screen, stack.get());
                });

                this.convertInternalBtn = new Button(x + 110, y + 16, 130, 20, new TranslationTextComponent(getTranslationKey("button.convert_to_tag")), BufferFactory.ping(2, update));

                this.removeModifiersNBT = new Button(x + 10, y + height - 23, 130, 20, new TranslationTextComponent(getTranslationKey("button.remove.tag")), BufferFactory.ping(3, update));
                this.removeModifiers = new Button(x + 145, y + height - 23, 130, 20, new TranslationTextComponent(getTranslationKey("button.remove.all")), BufferFactory.ping(4, update));

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
            public void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {
                FontRenderer font = screen.getMinecraft().fontRenderer;
                font.drawString(stackIn, "OP", x + 130 + MathHelper.clamp(width - 200, 40, 100), y + 25, 16777120);
                for (int i = 0; i < MODIFIERS.length; i++) {
                    String translationKey = "attribute.name." + MODIFIERS[i].attribute.get().getAttributeName();
                    font.func_243248_b(stackIn, I18n.hasKey(translationKey) ? new TranslationTextComponent(translationKey) : new StringTextComponent(MODIFIERS[i].attribute.get().getAttributeName()), x + 6, y + 42 + i * 17, 16777120);
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
                         boolean correctUUID = modifier.uuid == null || modifier.uuid.equals(attributemodifier.getID());

                         if (key.equals(modifier.attributeName) && correctOp && correctUUID) {
                             this.addBtnList.get(i).setMessage(new StringTextComponent("#"));

                             int scale = attributemodifier.getOperation() != AttributeModifier.Operation.ADDITION ? 100 : 1;
                             double amount = attributemodifier.getAmount() * scale;

                             this.valueInputList.get(i).setText(Util.tryToWriteAsInt(amount));
                             this.opBtnList.get(i).setValue(attributemodifier.getOperation());
                             this.removeBtnList.get(i).visible = true;
                             break MODIFIER;
                         }
                    }

                    // Only run if no modifier is found
                    this.addBtnList.get(i).setMessage(new StringTextComponent("+"));

                    this.valueInputList.get(i).setText("");
                    //this.opBtnList.get(i).setValue(AttributeModifier.Operation.ADDITION);
                    this.removeBtnList.get(i).visible = false;
                }

                this.removeModifiers.active = Arrays.stream(EquipmentSlotType.values()).anyMatch((slotType) -> !stack.getItem().getAttributeModifiers(slotType, stack).isEmpty());
                this.removeModifiersNBT.active = NBTUtil.hasTag(stack, "AttributeModifiers", Constants.NBT.TAG_LIST);
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

            public ITextComponent getOpString(AttributeModifier.Operation op) {
                switch(op) {
                case ADDITION:
                    return new StringTextComponent("+|");
                case MULTIPLY_BASE:
                    return new StringTextComponent("+%");
                case MULTIPLY_TOTAL:
                    return new StringTextComponent("x%");
                default:
                    return new StringTextComponent("??");
                }
            }
        };
    }

    public static AttributeModifier.Operation ADD_OPERATION = AttributeModifier.Operation.ADDITION;
    public static AttributeModifier.Operation MULT_PERCENTAGE_OPERATION = AttributeModifier.Operation.MULTIPLY_BASE;
    public static AttributeModifier.Operation ADD_PERCENTAGE_OPERATION = AttributeModifier.Operation.MULTIPLY_TOTAL;

    private Modifier ATTACK_DAMAGE = new Modifier(Item.ATTACK_DAMAGE_MODIFIER, Attributes.ATTACK_DAMAGE.delegate, "Weapon modifier");
    private Modifier ATTACK_SPEED = new Modifier(Item.ATTACK_SPEED_MODIFIER, Attributes.ATTACK_SPEED.delegate, "Weapon modifier");
    private Modifier ATTACK_KNOCKBACK = new Modifier(Attributes.ATTACK_KNOCKBACK.delegate, "Weapon modifier");
    private Modifier KNOCKBACK_RESISTANCE = new Modifier(Attributes.KNOCKBACK_RESISTANCE.delegate, "Knockback Resistance");
    private Modifier MAX_HEALTH = new Modifier(Attributes.MAX_HEALTH.delegate, "Max Health");
    private Modifier MOVEMENT_SPEED = new Modifier(Attributes.MOVEMENT_SPEED.delegate, "Movement Speed"); // MULT_PERCENTAGE_OPERATION
    private Modifier SPRINTING_SPEED_BOOST = new Modifier(LivingEntity.SPRINTING_SPEED_BOOST_ID, Attributes.MOVEMENT_SPEED.delegate, "Sprinting speed boost"); //MULTIPLY_TOTAL
    private Modifier FLYING_SPEED = new Modifier(Attributes.FLYING_SPEED.delegate, "Flying Speed"); // MULT_PERCENTAGE_OPERATION
    private Modifier FOLLOW_RANGE = new Modifier(Attributes.FOLLOW_RANGE.delegate, "Follow Range"); // MULT_PERCENTAGE_OPERATION
    private Modifier ARMOR = new Modifier(Attributes.ARMOR.delegate, "Armor modifier");
    private Modifier ARMOR_TOUGHNESS = new Modifier(Attributes.ARMOR_TOUGHNESS.delegate, "Armor toughness");
    private Modifier SPAWN_REINFORCEMENTS = new Modifier(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS.delegate, "Spawn Reinforcements Chance");
    private Modifier BABY_SPEED_BOOST = new Modifier(ZombieEntity.BABY_SPEED_BOOST_ID, Attributes.MOVEMENT_SPEED.delegate, "Baby speed boost");
    private Modifier HORSE_JUMP_STRENGTH = new Modifier(Attributes.HORSE_JUMP_STRENGTH.delegate, "Jump Strength");
    private Modifier HORSE_ARMOR = new Modifier(HorseEntity.ARMOR_MODIFIER_UUID, Attributes.ARMOR.delegate, "Horse armor bonus");

    // Potion Luck Modifier
    private Modifier LUCK = new Modifier(UUID.fromString("03C3C89D-7037-4B42-869F-B146BCB64D2E"), Attributes.LUCK.delegate, Effects.LUCK.getName());
    private Modifier UNLUCK = new Modifier(UUID.fromString("CC5AF142-2BD2-4215-B636-2605AED11727"), Attributes.LUCK.delegate, Effects.UNLUCK.getName());

    // Forge Modifiers
    private Modifier SLOW_FALLING = new Modifier(UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA"), ForgeMod.ENTITY_GRAVITY, "Slow falling acceleration reduction");
    private Modifier ENITTY_GRAVITY = new Modifier(ForgeMod.ENTITY_GRAVITY, "Gravity");
    private Modifier NAMETAG_DISTANCE = new Modifier(ForgeMod.NAMETAG_DISTANCE, "Name Tag");
    private Modifier SWIM_SPEED = new Modifier(ForgeMod.SWIM_SPEED, "Swim Speed");

    private final Modifier[] MODIFIERS = new Modifier[] {ATTACK_DAMAGE, ATTACK_SPEED, ATTACK_KNOCKBACK, KNOCKBACK_RESISTANCE, MAX_HEALTH, MOVEMENT_SPEED, SPRINTING_SPEED_BOOST, FLYING_SPEED, FOLLOW_RANGE, ARMOR, ARMOR_TOUGHNESS, LUCK, UNLUCK, SLOW_FALLING, ENITTY_GRAVITY, NAMETAG_DISTANCE, SWIM_SPEED, HORSE_ARMOR, HORSE_JUMP_STRENGTH, SPAWN_REINFORCEMENTS, BABY_SPEED_BOOST};

    public static class Modifier {

        public String attributeName;
        public Supplier<Attribute> attribute;
        @Nullable
        public UUID uuid;

        public Modifier(Supplier<Attribute> attribute, String name) {
            this(null, attribute, name);
        }

        public Modifier(UUID uuid, Supplier<Attribute> attribute, String name) {
            this.uuid = uuid;
            this.attribute = attribute;
            this.attributeName = name;
        }

        public AttributeModifier getEdited(double amountIn, AttributeModifier.Operation operationIn) {
            if (this.uuid == null) {
                return new AttributeModifier(this.attributeName, amountIn, operationIn);
            } else {
                return new AttributeModifier(this.uuid, this.attributeName, amountIn, operationIn);
            }
        }
    }
}
