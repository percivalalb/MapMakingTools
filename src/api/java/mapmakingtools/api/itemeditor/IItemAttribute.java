package mapmakingtools.api.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class IItemAttribute extends ForgeRegistryEntry<IItemAttribute> {

    @Nullable
    private String translationKey;

    /**
     * Creates and caches the translation key
     */
    public String getTranslationKey() {
        if (this.translationKey == null) {
           this.translationKey = Util.makeTranslationKey("item_editor", Registries.ITEM_ATTRIBUTES.getKey(this));
        }

        return this.translationKey;
    }

    public String getTranslationKey(String postfix) {
        return this.getTranslationKey() + "." + postfix;
    }

    /**
     * Controls if the button should be active in the editor
     * @param player The player editing the item
     * @param stack The stack in question
     * @return If the stack can have the given attribute
     */
    public boolean isApplicable(PlayerEntity player, ItemStack stack) {
        return this.isApplicable(player, stack.getItem());
    }

    public abstract boolean isApplicable(PlayerEntity player, Item item);

    /**
     *
     * @param stack A copy of the stack to edit
     * @param buffer Data sent from the client
     */
    public abstract ItemStack read(ItemStack stack, PacketBuffer buffer);

    /**
     * Contains the client side only methods, this includes GUI parts
     */
    public abstract Supplier<Callable<IItemAttributeClient>> client();
}
