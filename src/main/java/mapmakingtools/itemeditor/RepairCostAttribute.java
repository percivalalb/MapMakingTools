package mapmakingtools.itemeditor;

import com.google.common.base.Strings;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.client.screen.widget.WidgetFactory;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.util.NBTUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.Constants;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RepairCostAttribute extends IItemAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return true;
    }

    @Override
    public ItemStack read(ItemStack stack, FriendlyByteBuf buffer) {
        switch(buffer.readByte()) {
        case 0:
            int cost = buffer.readInt();

            if (cost == 0) {
                NBTUtil.removeTag(stack, "RepairCost", Constants.NBT.TAG_ANY_NUMERIC);
                NBTUtil.removeTagIfEmpty(stack);
            }
            else {
                stack.setRepairCost(cost);
            }
            return stack;
        default:
            throw new IllegalArgumentException("Received invalid type option in " + this.getClass().getSimpleName());
        }

    }

    @Override
    public Supplier<Callable<IItemAttributeClient>> client() {
        return () -> () -> new IItemAttributeClient() {

            private EditBox repairCostInput;

            @Override
            public void init(Screen screen, Consumer<AbstractWidget> add, Consumer<FriendlyByteBuf> update, Consumer<Integer> pauseUpdates, final ItemStack stack, int x, int y, int width, int height) {
                this.repairCostInput = WidgetFactory.getTextField(screen, x + 2, y + 15, width - 4, 13, this.repairCostInput, stack::getBaseRepairCost);

                this.repairCostInput.setMaxLength(3);
                this.repairCostInput.setResponder(BufferFactory.createInteger(0, Util.IS_NULL_OR_EMPTY.or(""::equals), update));
                this.repairCostInput.setFilter(Util.NUMBER_INPUT_PREDICATE);

                add.accept(this.repairCostInput);
            }

            @Override
            public void populateFrom(Screen screen, final ItemStack stack) {
                if (!Strings.isNullOrEmpty(this.repairCostInput.getValue())) {
                    WidgetUtil.setTextQuietly(this.repairCostInput, String.valueOf(stack.getBaseRepairCost()));
                }
            }

            @Override
            public void clear(Screen screen) {
                this.repairCostInput = null;
            }

            @Override
            public boolean requiresUpdate(ItemStack newStack, ItemStack oldStack) {
                return newStack.getBaseRepairCost() != oldStack.getBaseRepairCost();
            }
        };
    }

}
