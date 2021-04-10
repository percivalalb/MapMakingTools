package mapmakingtools.itemeditor;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.util.State;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class RecipeKnowledgeAttribute extends IItemAttribute  {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item == Items.KNOWLEDGE_BOOK;
    }

    @Override
    public ItemStack read(ItemStack stack, PacketBuffer buffer) {
        throw new UnsupportedOperationException("Attribute is WIP");
    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            @Override
            public void populateFrom(Screen screen, ItemStack stack) {}
        };
    }

    @Override
    public State getFeatureState() {
        return State.ALPHA;
    }
}
