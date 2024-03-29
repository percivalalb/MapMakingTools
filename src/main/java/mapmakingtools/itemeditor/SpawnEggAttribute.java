package mapmakingtools.itemeditor;

import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpawnEggAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item instanceof SpawnEggItem;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
            case 0:
                EntityType<?> entityType = buffer.readRegistryIdUnsafe(ForgeRegistries.ENTITIES);

                CompoundTag tag = NBTUtil.getOrCreateTag(stack);
                if (tag.contains("EntityTag", Tag.TAG_COMPOUND)) {
                    CompoundTag entityTag = tag.getCompound("EntityTag");

                }

                CompoundTag entityTagCreate = new CompoundTag();
                entityTagCreate.putString("id", entityType.getRegistryName().toString());
                tag.put("EntityTag", entityTagCreate);

                return stack;
            default:
                throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private ToggleBoxList<IRegistryDelegate<EntityType<?>>> entityTypeList;
            private Button addBtn;
            private String currentEntitySpawned;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.entityTypeList = new ToggleBoxList<>(x + 2, y + 32, width - 4, (height - 80) / 2, this.entityTypeList);
                this.entityTypeList.setSelectionGroupManager(new ToggleBoxGroup.Builder<IRegistryDelegate<EntityType<?>>>().min(1).max(1).build());
                this.entityTypeList.setValues(ForgeRegistries.ENTITIES.getValues().stream().map((entityType) -> entityType.delegate).collect(Collectors.toList()), IRegistryDelegate::name, this.entityTypeList);

                this.addBtn = new Button(x + 2, y + height / 2 - 3, 50, 20, new TranslatableComponent(getTranslationKey("button.set")), (btn) -> {
                    FriendlyByteBuf buf = Util.createBuf();
                    buf.writeByte(0);
                    List<IRegistryDelegate<EntityType<?>>> entityTypes = this.entityTypeList.getGroupManager().getSelected();
                    entityTypes.forEach((type) -> {
                        buf.writeRegistryIdUnsafe(ForgeRegistries.ENTITIES, type.get());
                    });
                    update.accept(buf);
                });


                add.accept(this.entityTypeList);
                add.accept(this.addBtn);;
            }

            @Override
            public void render(PoseStack stackIn, Screen screen, int x, int y, int width, int height) {
                Font font = screen.getMinecraft().font;
                font.draw(stackIn, new TranslatableComponent("item_editor.mapmakingtools.spawn_egg.entity_type", this.currentEntitySpawned), x + 2, y + 17, -1);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                CompoundTag tag = NBTUtil.getOrCreateTag(stack);
                if (tag.contains("EntityTag", Tag.TAG_COMPOUND)) {
                    CompoundTag entityTag = tag.getCompound("EntityTag");
                    if (entityTag.contains("id", Tag.TAG_STRING)) {
                        this.currentEntitySpawned = entityTag.getString("id");
                        ResourceLocation rl = ResourceLocation.tryParse(this.currentEntitySpawned);
                        if (rl != null && ForgeRegistries.ENTITIES.containsKey(rl)) {
                            this.entityTypeList.selectValue(ForgeRegistries.ENTITIES.getValue(rl).delegate);
                        }
                    }
                }

                if (this.currentEntitySpawned == null) {
                    Item item = stack.getItem();
                    if (item instanceof SpawnEggItem) {
                        // Pass null just to get the base type of the spawn egg
                        this.currentEntitySpawned = ((SpawnEggItem)item).getType(null).getRegistryName().toString();
                    }
                }
            }

            @Override
            public void clear(Screen screen) {
                this.entityTypeList = null;
                this.addBtn = null;
                this.currentEntitySpawned = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return true; // TODO
            }
        };
    }
}
