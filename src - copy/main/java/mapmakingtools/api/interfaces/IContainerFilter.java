package mapmakingtools.api.interfaces;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IContainerFilter {

	public void addSlot(Slot slot);
	public EntityPlayer getPlayer();
	public List<Slot> getInventorySlots();
	public boolean mergeItemStacks(ItemStack itemstack1, int j, int i, boolean b);
	public int getX();
	public int getY();
	public int getZ();
	public int getEntityId();
	public World getWorld();
	public IFilterServer getCurrentFilter();
}
