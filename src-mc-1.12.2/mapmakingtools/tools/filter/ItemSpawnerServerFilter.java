package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.FilterServerInventory;
import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.container.SlotFake;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class ItemSpawnerServerFilter extends FilterServerInventory {
	
	@Override
	public void addSlots(IContainerFilter container) {
        container.addSlot(new SlotFake(getInventory(container), 0, 23, 37));
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j) {
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 59 + j * 18, 23 + i * 18));
	        }
		}	

	    for (int i = 0; i < 9; ++i) {
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 59 + i * 18, 81));
	    }
	}

	@Override
	public String getSaveId() { 
		return "changeItem"; 
	}
	
	@Override
	public IInventory createInventory() {
		return new InventoryBasic("Item Spawner", false, 1);
	}
}
