package mapmakingtools.itemeditor;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.util.State;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class RecipeKnowledgeAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item == Items.KNOWLEDGE_BOOK;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException("Attribute is WIP");
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            @Override
            public void populateFrom(Screen screen, ItemStack stack) {
            }
        };
    }

    @Override
    public State getFeatureState() {
        return State.ALPHA;
    }
}
