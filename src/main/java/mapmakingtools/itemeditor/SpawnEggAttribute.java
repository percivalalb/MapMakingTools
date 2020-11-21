package mapmakingtools.itemeditor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.ToggleBoxList;
import mapmakingtools.client.screen.widget.ToggleBoxList.ToggleBoxGroup;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

public class SpawnEggAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item instanceof SpawnEggItem;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        switch(buffer.readByte()) {
            case 0:
                EntityType<?> entityType = buffer.readRegistryIdUnsafe(ForgeRegistries.ENTITIES);

                CompoundNBT tag = NBTUtil.getOrCreateTag(stack);
                if (tag.contains("EntityTag", Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT entityTag = tag.getCompound("EntityTag");

                }

                CompoundNBT entityTagCreate = new CompoundNBT();
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
            public void init(Screen screen, Consumer<Widget> add, Consumer<PacketBuffer> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.entityTypeList = new ToggleBoxList<>(x + 2, y + 32, width - 4, (height - 80) / 2, this.entityTypeList);
                this.entityTypeList.setSelectionGroupManager(new ToggleBoxGroup.Builder<IRegistryDelegate<EntityType<?>>>().min(1).max(1).build());
                this.entityTypeList.setValues(ForgeRegistries.ENTITIES.getValues().stream().map((entityType) -> entityType.delegate).collect(Collectors.toList()), IRegistryDelegate::name, this.entityTypeList);

                this.addBtn = new Button(x + 2, y + height / 2 - 3, 50, 20, new TranslationTextComponent(getTranslationKey("button.set")), (btn) -> {
                    PacketBuffer buf = Util.createBuf();
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
            public void render(MatrixStack stackIn, Screen screen, int x, int y, int width, int height) {
                FontRenderer font = screen.getMinecraft().fontRenderer;
                font.func_243248_b(stackIn, new TranslationTextComponent("item_editor.mapmakingtools.spawn_egg.entity_type", this.currentEntitySpawned), x + 2, y + 17, -1);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                CompoundNBT tag = NBTUtil.getOrCreateTag(stack);
                if (tag.contains("EntityTag", Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT entityTag = tag.getCompound("EntityTag");
                    if (entityTag.contains("id", Constants.NBT.TAG_STRING)) {
                        this.currentEntitySpawned = entityTag.getString("id");
                        ResourceLocation rl = ResourceLocation.tryCreate(this.currentEntitySpawned);
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
