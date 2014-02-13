package mapmakingtools.container;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.IContainerFilter;
import mapmakingtools.api.IFilterServer;
import mapmakingtools.api.TargetType;
import mapmakingtools.tools.filter.packet.PacketPhantomInfinity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class ContainerFilter extends Container implements IContainerFilter {

	//Extra data the class holds
	public EntityPlayer player;
	public int x, y, z;
	public int entityId;
	public TargetType mode;
	
	public List<IFilterServer> filterList;
	public IFilterServer filterCurrent;
	public int selected;
	
	public ContainerFilter(List<IFilterServer> filterList, EntityPlayer player) {
		//this.inventorySlots = new ArrayListSlot();
		this.filterList = filterList;
		this.player = player;
	}
	
	public ContainerFilter setEntityId(int entityId) {
		this.entityId = entityId;
		return this;
	}
	
	public ContainerFilter setBlockCoords(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public void setSelected(int selected) {
		this.selected = selected;
	    this.inventorySlots.clear();
	    this.inventoryItemStacks.clear();
	    this.filterCurrent = this.filterList.get(selected);
	    if(this.filterCurrent != null)
	    	this.filterCurrent.addSlots(this);
	}
	
	@Override
	public void addSlot(Slot slot) {
		this.addSlotToContainer(slot);
	}
	
	@Override
	public Slot addSlotToContainer(Slot slot) {
		slot.xDisplayPosition += 62 / 2;
        slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slot);
        this.inventoryItemStacks.add((Object)null);
        if(slot instanceof IPhantomSlot)
        	MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketPhantomInfinity(slot.getSlotIndex(), ((IPhantomSlot)slot).isUnlimited()), player);
        return slot;
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    	ItemStack stack = null;
    	if(filterCurrent != null && (stack = filterCurrent.transferStackInSlot(this, par1EntityPlayer, par2)) != null) {
    		return stack;
    	}
    	else {
    		return null;
    	}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public EntityPlayer getPlayer() {
		return this.player;
	}

	@Override
	public Slot getSlot(int par1) {
		if(par1 >= 0 && par1 < inventorySlots.size()) {
			return (Slot)this.inventorySlots.get(par1);
		}
		return null;
    }
	
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
        for (int i = 0; i < par1ArrayOfItemStack.length; ++i) {
            Slot slot = this.getSlot(i);
            if(slot != null) {
            	slot.putStack(par1ArrayOfItemStack[i]);
            }
        }
    }
	
	@Override
	public void putStackInSlot(int par1, ItemStack par2ItemStack) {
		Slot slot = this.getSlot(par1);
        if(slot != null) {
        	slot.putStack(par2ItemStack);
        }
	}

	//Phantom Slot
	@Override
	public ItemStack slotClick(int slotNum, int mouseButton, int modifier, EntityPlayer player) {
		Slot slot = slotNum < 0 || slotNum >= inventorySlots.size() ? null : (Slot) this.inventorySlots.get(slotNum);
		if (slot instanceof IPhantomSlot) {
			return slotClickPhantom(slot, mouseButton, modifier, player);
		}
		return super.slotClick(slotNum, mouseButton, modifier, player);
	}

	private ItemStack slotClickPhantom(Slot slot, int mouseButton, int modifier, EntityPlayer player) {
		if(player.worldObj.isRemote)
			return null;
		ItemStack stack = null;
		IPhantomSlot phantomSlot = (IPhantomSlot)slot;
		InventoryPlayer playerInv = player.inventory;
		ItemStack stackHeld = playerInv.getItemStack();
		ItemStack slotStack = slot.getStack();
		
		if(stackHeld != null) {
			ItemStack phantomStack = stackHeld.copy();
			phantomStack.stackSize = slotStack != null ? slotStack.stackSize : 1;
			slot.putStack(phantomStack);
		}
		else if(slotStack != null) {
			
			int stackSize = mouseButton == 1 ? slotStack.stackSize + 1: slotStack.stackSize - 1;
		
			if(stackSize == 2 && phantomSlot.isUnlimited()) {
				stackSize = 1;
				if(slot.inventory instanceof IUnlimitedInventory)
					((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
				phantomSlot.setIsUnlimited(false);
				MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
			}
			
			if(stackSize < 1 && !phantomSlot.isUnlimited()) {
				stackSize = 1;
				if(slot.inventory instanceof IUnlimitedInventory)
					((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), true);
				phantomSlot.setIsUnlimited(true);
				MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketPhantomInfinity(slot.getSlotIndex(), true), player);
			}
			slotStack.stackSize = stackSize;
			if(stackSize < 1) {
				slot.putStack(null);
				if(slot.inventory instanceof IUnlimitedInventory)
					((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
				phantomSlot.setIsUnlimited(false);
				MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
			}
		}

		return stack;
	}

	@Override
	public List<Slot> getInventorySlots() {
		return this.inventorySlots;
	}

	@Override
	public boolean mergeItemStacks(ItemStack itemstack1, int j, int i, boolean b) {
		return this.mergeItemStack(itemstack1, j, i, b);
	}
}
