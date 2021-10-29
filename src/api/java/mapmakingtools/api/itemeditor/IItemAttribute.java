package mapmakingtools.api.itemeditor;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import mapmakingtools.api.util.IFeatureState;
import mapmakingtools.api.util.State;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.Util;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class IItemAttribute extends ForgeRegistryEntry<IItemAttribute> implements IFeatureState {

    @Nullable
    private String translationKey;

    /**
     * Creates and caches the translation key
     */
    public String getTranslationKey() {
        if (this.translationKey == null) {
           this.translationKey = Util.makeDescriptionId("item_editor", Registries.ITEM_ATTRIBUTES.getKey(this));
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
    public boolean isApplicable(Player player, ItemStack stack) {
        return this.isApplicable(player, stack.getItem());
    }

    public abstract boolean isApplicable(Player player, Item item);

    /**
     *
     * @param stack A copy of the stack to edit
     * @param buffer Data sent from the client
     */
    @Deprecated // Call player sensitive version
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer, @Nullable Player player) {
        return this.read(stack, buffer);
    }

    /**
     * Contains the client side only methods, this includes GUI parts
     */
    public abstract Supplier<Callable<IItemAttributeClient>> client();
}
