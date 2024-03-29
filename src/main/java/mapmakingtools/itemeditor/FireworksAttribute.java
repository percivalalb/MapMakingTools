package mapmakingtools.itemeditor;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.TextUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FireworksAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item == Items.FIREWORK_ROCKET || item == Items.FIREWORK_STAR;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            CompoundTag fireworkNBT = NBTUtil.getOrCreateSubCompound(stack, "Fireworks");
            fireworkNBT.putInt("Flight", buffer.readInt());
            return stack;
        case 1:
            CompoundTag fireworkNBT2 = NBTUtil.getOrCreateSubCompound(stack, "Fireworks");
            if (!fireworkNBT2.contains("Explosions", Tag.TAG_LIST)) {
                fireworkNBT2.put("Explosions", new ListTag());
            }

            ListTag explosionListNBT = fireworkNBT2.getList("Explosions", Tag.TAG_COMPOUND);

            CompoundTag newExplosion = new CompoundTag();
            newExplosion.putByte("Type", buffer.readByte());
            newExplosion.putIntArray("Colors", buffer.readVarIntArray());
            newExplosion.putIntArray("FadeColors", buffer.readVarIntArray());
            newExplosion.putBoolean("Trail", buffer.readBoolean());
            newExplosion.putBoolean("Flicker", buffer.readBoolean());
            explosionListNBT.add(newExplosion);
            return stack;
        case 2:
            if (NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", Tag.TAG_LIST)) {
                CompoundTag fireworkNBT3 = stack.getTag().getCompound("Fireworks");
                ListTag explosionsNBT = fireworkNBT3.getList("Explosions", Tag.TAG_COMPOUND);
                int amount = buffer.readInt();
                for (int i = 0; i < amount; i++) {
                    explosionsNBT.remove(buffer.readByte());
                }

                //Remove empty NBT data
                if (explosionsNBT.isEmpty()) {
                    fireworkNBT3.remove("Explosions");
                    if (fireworkNBT3.isEmpty()) {
                        stack.getTag().remove("Fireworks");
                    }
                }
                NBTUtil.removeTagIfEmpty(stack);
            }
            return stack;
        case 3:
            if (NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", Tag.TAG_LIST)) {
                NBTUtil.removeTagFromSubCompound(stack, "Fireworks", Tag.TAG_LIST, "Explosions");
                NBTUtil.removeTagIfEmpty(stack);
            }
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    public List<Byte> getExplosionEffects(ItemStack stack) {
        List<Byte> explosions = Lists.newArrayList();
        if (NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", Tag.TAG_LIST)) {
            ListTag explosionList = stack.getTag().getCompound("Fireworks").getList("Explosions", Tag.TAG_COMPOUND);
            for (int i = 0; i < explosionList.size(); ++i) {
                CompoundTag t = explosionList.getCompound(i);
                explosions.add((byte) i);
            }
        }

        return explosions;
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private EditBox flightInput;
            private ToggleBoxList<FireworkRocketItem.Shape> typeList;
            private ToggleBoxList<DyeColor> mainColorsList;
            private ToggleBoxList<DyeColor> fadeColorsList;
            private ToggleBoxList<Byte> currentEffectsList;
            private Checkbox trailBtn, flickerBtn;
            private Button addBtn, removeBtn, removeAllBtn;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.flightInput = WidgetFactory.getTextField(screen, x + 84, y + 15, 80, 13, this.flightInput, stack::getDamageValue);
                this.flightInput.setMaxLength(3);
                this.flightInput.setResponder(BufferFactory.createInteger(0, Util.IS_NULL_OR_EMPTY.or(""::equals), update));
                this.flightInput.setFilter(Util.NUMBER_INPUT_PREDICATE);

                this.typeList = new ToggleBoxList<>(x + 2, y + 42, 100, 14 * 5, this.typeList);
                this.typeList.setSelectionGroupManager(ToggleBoxGroup.builder(FireworkRocketItem.Shape.class).min(1).max(1).listen((selection) -> {
                    this.addBtn.active = !selection.isEmpty();
                }).build());
                this.typeList.setValues(FireworkRocketItem.Shape.values(), (v) -> new TranslatableComponent("item.minecraft.firework_star.shape." + v.getName()), this.typeList);

                int colourMenuWidth = Math.max((width - 105) / 2, 82);

                this.mainColorsList = new ToggleBoxList<>(x + 105, y + 42, colourMenuWidth, 14 * 5, this.mainColorsList);
                this.mainColorsList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.mainColorsList.setValues(DyeColor.values(), (v) -> new TranslatableComponent(v.getName()), this.mainColorsList);

                this.fadeColorsList = new ToggleBoxList<>(x + 106 + colourMenuWidth, y + 42, colourMenuWidth, 14 * 5, this.fadeColorsList);
                this.fadeColorsList.setSelectionGroupManager(ToggleBoxGroup.noLimits());
                this.fadeColorsList.setValues(DyeColor.values(), (v) -> new TranslatableComponent(v.getName()), this.fadeColorsList);

                this.currentEffectsList = new ToggleBoxList<>(x + 2, y + 25 + height / 2, width - 4, height / 2 - 35 - y, this.currentEffectsList);
                this.currentEffectsList.setSelectionGroupManager(ToggleBoxGroup.builder(Byte.class).min(0).max(Integer.MAX_VALUE).listen((selection) -> {
                    this.removeBtn.active = !selection.isEmpty();
                }).build());
                this.currentEffectsList.setValues(getExplosionEffects(stack), Integer::toString, this.currentEffectsList);

                this.trailBtn = new Checkbox(x + 34, y + 14 * 5 + 43, 20, 20, TextUtil.EMPTY, false);
                this.flickerBtn = new Checkbox(x + 101, y + 14 * 5 + 43, 20, 20, TextUtil.EMPTY, false);

                this.addBtn = new SmallButton(x + width - 90, y + 14 * 5 + 45, 80, 16, new TranslatableComponent(getTranslationKey("button.add")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(1);

                    buf.writeByte((byte) this.typeList.getGroupManager().getSelected().get(0).getId());
                    Util.writeVarIntArray(buf, this.mainColorsList.getGroupManager().map(DyeColor::getFireworkColor).toArray(Integer[]::new));
                    Util.writeVarIntArray(buf, this.fadeColorsList.getGroupManager().map(DyeColor::getFireworkColor).toArray(Integer[]::new));
                    buf.writeBoolean(this.trailBtn.selected());
                    buf.writeBoolean(this.flickerBtn.selected());

                    update.accept(buf);
                });

                this.removeBtn = new Button(x + 60, y + height - 23, 60, 20, new TranslatableComponent(getTranslationKey("button.remove")), (btn) -> {
                    btn.active = !this.currentEffectsList.getGroupManager().isEmpty();
                    this.removeAllBtn.active = !this.currentEffectsList.isEmpty();
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(2);
                    List<Byte> fireworkEffects = this.currentEffectsList.getGroupManager().getSelected();
                    Collections.sort(fireworkEffects);
                    buf.writeInt(fireworkEffects.size());
                    for (int index = fireworkEffects.size() - 1; index >= 0; index--) {
                        buf.writeByte(index);
                    };
                    update.accept(buf);
                });
                this.removeAllBtn = new Button(x + 130, y + height - 23, 130, 20, new TranslatableComponent(getTranslationKey("button.remove.all")), BufferFactory.ping(3, update));

                add.accept(this.flightInput);
                add.accept(this.typeList);
                add.accept(this.mainColorsList);
                add.accept(this.fadeColorsList);
                add.accept(this.currentEffectsList);
                add.accept(this.trailBtn);
                add.accept(this.flickerBtn);
                add.accept(this.addBtn);
                add.accept(this.removeBtn);
                add.accept(this.removeAllBtn);
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
                Font font = screen.getMinecraft().font;
                font.draw(stackIn, new TranslatableComponent("item.minecraft.firework_rocket.flight"), x + 6, y + 17, 16777120);
                font.draw(stackIn, new TranslatableComponent(getTranslationKey("shape")), x + 4, y + 32, 16777120);
                font.draw(stackIn, new TranslatableComponent(getTranslationKey("color")), x + 108, y + 32, 16777120);
                font.draw(stackIn, new TranslatableComponent(getTranslationKey("fade_color")), x + 109 + Math.max((width - 105) / 2, 82), y + 32, 16777120);
                font.draw(stackIn, new TranslatableComponent(getTranslationKey("trail")), x + 6, y + 14 * 5 + 47, 16777120);
                font.draw(stackIn, new TranslatableComponent(getTranslationKey("flicker")), x + 63, y + 14 * 5 + 47, 16777120);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (NBTUtil.hasTag(stack, "Fireworks", Tag.TAG_COMPOUND)) {
                    CompoundTag fireworkNBT = stack.getTag().getCompound("Fireworks");

                    WidgetUtil.setTextQuietly(this.flightInput, "" + fireworkNBT.getByte("Flight"));
                }

                this.currentEffectsList.setValues(getExplosionEffects(stack), Integer::toString, this.currentEffectsList);

                this.updateButtonStatuses();
            }

            public void updateButtonStatuses() {
                this.addBtn.active = !this.typeList.getGroupManager().isEmpty();
                this.removeBtn.active = !this.currentEffectsList.getGroupManager().isEmpty();
                this.removeAllBtn.active = !this.currentEffectsList.isEmpty();
            }

            @Override
            public void clear(Screen screen) {
                this.flightInput = null;
                this.flightInput = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return true; // TODO
            }
        };
    }

}
