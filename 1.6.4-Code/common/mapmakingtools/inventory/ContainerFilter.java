package mapmakingtools.inventory;

import java.util.List;

import mapmakingtools.api.IFilter;
import mapmakingtools.api.IPhantomSlot;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.util.ArrayListSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFilter extends Container {
	
	//Extra data the class holds
	public EntityPlayer player;
    public int x, y, z;
    public int entityId;
    
    //The current page the client is on
	public int selected;
	//The list that the current screen could be
	public List<IServerFilter> filters;
	public IServerFilter current;
	
	public ContainerFilter(EntityPlayer player, List<IServerFilter> filters) {
		this.inventorySlots = new ArrayListSlot();
		this.filters = filters;
		this.player = player;
	    if(!filters.contains(current)) {
	    	current = null;
	    }
	    if(current != null) {
	    	current.addSlots(this);
	    }
	}

	public ContainerFilter setEntityId(int entityId) {
		this.entityId = entityId;
		return this;
	}
	
	public ContainerFilter setCor(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	
	public void setSelected(int sel) {
		this.selected = sel;
	    this.inventorySlots.clear();
	    this.inventoryItemStacks.clear();
	    this.current = filters.get(sel);
	    if(current != null) {
	    	current.addSlots(this);
	    }
	}
	
	@Override
	public Slot addSlotToContainer(Slot par1Slot) {
		par1Slot.xDisplayPosition += 62 / 2;
        par1Slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(par1Slot);
        this.inventoryItemStacks.add((Object)null);
        return par1Slot;
    }
	
	@Override
	public boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4) {
		return super.mergeItemStack(par1ItemStack, par2, par3, par4);
	}

	@Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    	ItemStack stack = null;
    	if(current != null && (stack = current.transferStackInSlot(this, par1EntityPlayer, par2)) != null) {
    		return stack;
    	}
    	else {
    		return null;
    	}
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
		ItemStack stack = null;

		if (mouseButton == 2) {
			if (((IPhantomSlot)slot).canAdjust()) {
				slot.putStack(null);
			}
		} else if (mouseButton == 0 || mouseButton == 1) {
			InventoryPlayer playerInv = player.inventory;
			slot.onSlotChanged();
			ItemStack stackSlot = slot.getStack();
			ItemStack stackHeld = playerInv.getItemStack();

			if (stackSlot != null) {
				stack = stackSlot.copy();
			}

			if (stackSlot == null) {
				if (stackHeld != null && slot.isItemValid(stackHeld)) {
					fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
				}
			} else if (stackHeld == null) {
				adjustPhantomSlot(slot, mouseButton, modifier);
				slot.onPickupFromSlot(player, playerInv.getItemStack());
			} else if (slot.isItemValid(stackHeld)) {
				if (ItemStackHelper.canStacksMerge(stackSlot, stackHeld)) {
					adjustPhantomSlot(slot, mouseButton, modifier);
				} else {
					fillPhantomSlot(slot, stackHeld, mouseButton, modifier);
				}
			}
		}
		return stack;
	}

	protected void adjustPhantomSlot(Slot slot, int mouseButton, int modifier) {
		if (!((IPhantomSlot) slot).canAdjust()) {
			return;
		}
		ItemStack stackSlot = slot.getStack();
		int stackSize;
		if (modifier == 1) {
			stackSize = mouseButton == 0 ? (stackSlot.stackSize + 1) / 2 : stackSlot.stackSize * 2;
		} else {
			stackSize = mouseButton == 0 ? stackSlot.stackSize - 1 : stackSlot.stackSize + 1;
		}

		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}

		stackSlot.stackSize = stackSize;

		if (stackSlot.stackSize <= 0) {
			slot.putStack(null);
		}
	}

	protected void fillPhantomSlot(Slot slot, ItemStack stackHeld, int mouseButton, int modifier) {
		if (!((IPhantomSlot)slot).canAdjust()) {
			return;
		}
		int stackSize = mouseButton == 0 ? stackHeld.stackSize : 1;
		if (stackSize > slot.getSlotStackLimit()) {
			stackSize = slot.getSlotStackLimit();
		}
		ItemStack phantomStack = stackHeld.copy();
		phantomStack.stackSize = stackSize;
		slot.putStack(phantomStack);
	}
}
