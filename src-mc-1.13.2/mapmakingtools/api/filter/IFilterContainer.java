package mapmakingtools.api.filter;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public interface IFilterContainer extends IFilterBase {

	public void addSlotForFilter(Slot slot);
	public List<Slot> getInventorySlots();
	public boolean mergeItemStacks(ItemStack itemstack1, int j, int i, boolean b);

	public FilterServer getCurrentFilter();
}
