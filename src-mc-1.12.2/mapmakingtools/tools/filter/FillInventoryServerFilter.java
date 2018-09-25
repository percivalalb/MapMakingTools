package mapmakingtools.tools.filter;

import mapmakingtools.api.filter.FilterServerInventory;
import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.inventory.slot.SlotFake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FillInventoryServerFilter extends FilterServerInventory {
	
	@Override
	public void addSlots(IFilterContainer container) {
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
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity != null && tileEntity instanceof IInventory)
			return true;
		return super.isApplicable(player, world, pos);
	}
	
	@Override
	public String getSaveId() { 
		return "fillInventory"; 
	}

	@Override
	public IInventory createInventory() {
		return new InventoryBasic("Fill Inventory", false, 1);
	}
}
