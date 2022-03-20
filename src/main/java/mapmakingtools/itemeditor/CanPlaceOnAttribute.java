package mapmakingtools.itemeditor;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CanPlaceOnAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    public String getNBTName() {
        return "CanPlaceOn";
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            int amount = buffer.readInt();
            ListTag blockPlaceList = NBTUtil.getOrCreateSubList(stack, this.getNBTName(), Tag.TAG_STRING);

            for (int i = 0; i < amount; i++) {
//                StringBuilder stringbuilder = new StringBuilder(block.getRegistryName().toString());
//                if (buffer.readBoolean()) {
//                   stringbuilder.append('[');
//                   boolean flag = false;
//
//                   int noProps = buffer.readInt();
//                   for (int p = 0; p < noProps; p++) {
//                      if (flag) {
//                         stringbuilder.append(',');
//                      }
//
//                      String propName = buffer.readString();
//                      stringbuilder.append(propName);
//                      stringbuilder.append('=');
//                      boolean flag1 = false;
//                      int noPropValues = buffer.readInt();
//                      for (int v = 0; v < noPropValues; v++) {
//                          if (flag1) {
//                              stringbuilder.append(',');
//                           }
//                          stringbuilder.append(buffer.readString());
//                          flag1 = true;
//                      }
//                      flag = true;
//                   }
//
//                   stringbuilder.append(']');
//                }

                Block block = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
//                BlockState blockState = block.getDefaultState();
//                if (buffer.readBoolean()) {
//                    int noProps = buffer.readInt();
//                    for (int p = 0; p < noProps; p++) {
//                        String propertyStr = buffer.readString(256);
//                        String valueStr = buffer.readString(256);
//                        this.applyPropertyValue(blockState, propertyStr, valueStr);
//                    }
//
//                }
//                String parse = BlockStateParser.toString(blockState);
                String parse = block.getRegistryName().toString();
                NBTUtil.addToSet(blockPlaceList, parse, Tag.TAG_STRING);
            }
            return stack;
        case 1:
            int amount2 = buffer.readInt();
            ListTag blockPlaceList2 = NBTUtil.getOrCreateSubList(stack, this.getNBTName(), Tag.TAG_STRING);

            for (int i = 0; i < amount2; i++) {
                ResourceLocation blockTag = buffer.readResourceLocation();
                NBTUtil.addToSet(blockPlaceList2, "#" + blockTag, Tag.TAG_STRING);
            }
            return stack;
        case 2:
            if (NBTUtil.hasTag(stack, this.getNBTName(), Tag.TAG_LIST)) {
                ListTag list = stack.getTag().getList(this.getNBTName(), Tag.TAG_STRING);

                int amount1 = buffer.readInt();
                for (int i = 0; i < amount1; i++) {
                    String blockStateStr = buffer.readUtf(1024);
                    for (int j = 0; j < list.size(); j++) {
                        if (blockStateStr.equals(list.getString(j))) {
                            list.remove(j);
                            break;
                        }
                    }
                }

                if (list.isEmpty()) {
                    stack.getTag().remove(this.getNBTName());
                }

                NBTUtil.removeTagIfEmpty(stack);
            }
            return stack;
        case 3:
            if (NBTUtil.hasTag(stack, this.getNBTName(), Tag.TAG_LIST)) {
                stack.getTag().remove(this.getNBTName());
                NBTUtil.removeTagIfEmpty(stack);
            }
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> void applyPropertyValue(BlockState blockState, String propertyStr, String valueStr) {
        Property<T> property = (Property<T>) blockState.getBlock().getStateDefinition().getProperty(propertyStr);
        property.getValue(valueStr).ifPresent(value -> { blockState.setValue(property, value); });
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> String getPropertyValueName(Property<T> property, Object value) {
        return property.getName((T) value);
    }

    public List<String> getBlocks(ItemStack stack) {
        List<String> blocks = Lists.newArrayList();
        if (NBTUtil.hasTag(stack, this.getNBTName(), Tag.TAG_LIST)) {
            ListTag blockPlaceList = stack.getTag().getList(this.getNBTName(), Tag.TAG_STRING);
            for (int i = 0; i < blockPlaceList.size(); ++i) {
                try {
                    BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(blockPlaceList.getString(i)), true)).parse(true);

                    blocks.add(blockPlaceList.getString(i));

                } catch (CommandSyntaxException e) {

                }
            }
        }

        return blocks;
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private ToggleBoxList<Block> blockList;
            private ToggleBoxList<ResourceLocation> tagList;
//            private ScrollWidget<Property<?>> blockPropertiesList;
//            private ScrollWidget<String> blockPropertyValuesList;
            private ToggleBoxList<String> currentPlacableList;
            private Button blockTagBtn, addBtn, removeBtn, removeAllBtn;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.blockList = new ToggleBoxList<>(x + 2, y + 12, 200, (height - 80) / 2, this.blockList);
                this.blockList.setSelectionGroupManager(ToggleBoxGroup.builder(Block.class).min(1).max(1).listen((selection) -> {
                    if (!selection.isEmpty()) {
                        List<Property<?>> p = Lists.newArrayList();
                        selection.get(0).getStateDefinition().getProperties().forEach(p::add);
//                        this.blockPropertiesList.setValues(p, Property::getName, this.blockPropertiesList);
//                        this.blockPropertyValuesList.clear();
                    }
                    updateAddButton();
                }).build());
                this.blockList.setValues(ForgeRegistries.BLOCKS.getValues(), Block::getRegistryName, this.blockList);

                this.tagList = new ToggleBoxList<>(x + 2, y + 12, 200, (height - 80) / 2, this.tagList);
                this.tagList.setSelectionGroupManager(ToggleBoxGroup.builder(ResourceLocation.class).min(0).max(Integer.MAX_VALUE).listen((selection) -> {
                    updateAddButton();
                }).build());
                this.tagList.setValues(ForgeRegistries.BLOCKS.tags(), this.tagList);
                this.tagList.visible = false;

//                this.blockPropertiesList = new ScrollWidget<>(x + 200, y + 12, 200, (height - 80) / 2, this.blockPropertiesList);
//                this.blockPropertiesList.setSelectionGroupManager(ToggleBoxGroup.builderProperty().min(1).max(1).listen((selection) -> {
//                    if (!selection.isEmpty()) {
//                        this.blockPropertyValuesList.setValues(selection.get(0).getAllowedValues(), (v) -> v.toString(), this.blockPropertyValuesList);
//                    }
//                }).build());
//
//                this.blockPropertyValuesList = new ScrollWidget<>(x + 400, y + 12, width - 4, (height - 80) / 2, this.blockPropertyValuesList);
//                this.blockPropertyValuesList.setSelectionGroupManager(ToggleBoxGroup.default1());

                this.currentPlacableList = new ToggleBoxList<>(x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, this.currentPlacableList);
                this.currentPlacableList.setSelectionGroupManager(ToggleBoxGroup.builder(String.class).min(0).max(Integer.MAX_VALUE).listen((selection) -> { this.removeBtn.active = !selection.isEmpty(); }).build());
                this.currentPlacableList.setValues(getBlocks(stack), Objects::toString, this.currentPlacableList);

                this.blockTagBtn = new Button(x + 2, y + height / 2 - 23, 50, 20, new TranslatableComponent(getTranslationKey("button.tag")), (btn) -> {
                    this.blockList.visible = this.tagList.visible;
//                    this.blockPropertiesList.visible = this.blockList.visible;
                    this.tagList.visible = !this.tagList.visible;
                    this.updateAddButton();
                    btn.setMessage(new TranslatableComponent(getTranslationKey(this.blockList.visible ? "button.tag" : "button.block")));
                });

                this.addBtn = new Button(x + 60, y + height / 2 - 23, 50, 20, new TranslatableComponent(getTranslationKey("button.add")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();


                    if (this.blockList.visible) {
                        buf.writeByte(0);

                        List<Block> blocksSelected = this.blockList.getGroupManager().getSelected();
//                        List<Property<?>> blockProperties = this.blockPropertiesList.getGroupManager().getSelected();
                        List<? extends Comparable<?>> blockPropertyValues = Lists.newArrayList();// this.blockPropertyValuesList.getGroupManager().getSelected();
                        buf.writeInt(blocksSelected.size());
                        blocksSelected.forEach(ench -> {
                            buf.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, ench);

//                            if (!blockProperties.isEmpty() && !blockPropertyValues.isEmpty()) {
//                                buf.writeBoolean(true);
//                                buf.writeInt(blockProperties.size());
//                                for (Property<?> property : blockProperties) {
//                                    buf.writeString(property.getName(), 256);
//                                    buf.writeInt(blockPropertyValues.size());
//                                    for (Comparable<?> value : blockPropertyValues) {
//                                        buf.writeString(getPropertyValueName(property, value), 256);
//                                    }
//                                }
//                            } else {
//                                buf.writeBoolean(false);
//                            }
                        });
                    } else {
                        buf.writeByte(1);

                        List<ResourceLocation> tagsSelected = this.tagList.getGroupManager().getSelected();
                        buf.writeInt(tagsSelected.size());
                        tagsSelected.forEach(r -> {
                            buf.writeResourceLocation(r);
                        });
                    }
                    update.accept(buf);
                });
                this.addBtn.active = false;

                this.removeBtn = new Button(x + 60, y + height - 23, 60, 20, new TranslatableComponent(getTranslationKey("button.remove")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(2);
                    List<String> blockStates = this.currentPlacableList.getGroupManager().getSelected();
                    buf.writeInt(blockStates.size());
                    blockStates.forEach(block -> {
                        buf.writeUtf(block, 1024);
                    });
                    update.accept(buf);
                });

                this.removeAllBtn = new Button(x + 130, y + height - 23, 130, 20, new TranslatableComponent(getTranslationKey("button.remove.all")), BufferFactory.ping(3, update));

                add.accept(this.blockList);
                add.accept(this.tagList);
//                add.accept(this.blockPropertiesList);
//                add.accept(this.blockPropertyValuesList);
                add.accept(this.currentPlacableList);
                add.accept(this.blockTagBtn);
                add.accept(this.addBtn);
                add.accept(this.removeBtn);
                add.accept(this.removeAllBtn);
            }

            public void updateAddButton() {
                if (this.blockList.visible) {
                    this.addBtn.active = !this.blockList.getGroupManager().getSelected().isEmpty();
                } else if (this.tagList.visible) {
                    this.addBtn.active = !this.tagList.getGroupManager().getSelected().isEmpty();
                }
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                this.currentPlacableList.setValues(getBlocks(stack), Objects::toString, this.currentPlacableList);
                this.removeBtn.active = false;
                this.removeAllBtn.active = this.currentPlacableList.getNoElements() > 0;
            }

            @Override
            public void clear(Screen screen) {
                this.blockList = null;
//                this.blockPropertiesList = null;
//                this.blockPropertyValuesList.clear();
                this.currentPlacableList = null;
                this.addBtn = null;
                this.removeBtn = null;
                this.removeAllBtn = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                boolean newHas = NBTUtil.hasTag(newStack, getNBTName(), Tag.TAG_LIST);
                boolean oldHas = NBTUtil.hasTag(oldStack, getNBTName(), Tag.TAG_LIST);

                if (newHas != oldHas) {
                    return true;
                } else if (!newHas) {
                    return false;
                }

                return !newStack.getTag().getList(getNBTName(), Tag.TAG_STRING)
                        .equals(oldStack.getTag().getList(getNBTName(), Tag.TAG_STRING));
            }
        };
    }
}
