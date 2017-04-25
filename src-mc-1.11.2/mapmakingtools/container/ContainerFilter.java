package mapmakingtools.container;

import java.util.List;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketPhantomInfinity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class ContainerFilter extends Container implements IContainerFilter {

	//Extra data the class holds
	public EntityPlayer player;
	public BlockPos pos;
	public int entityId;
	public TargetType mode;
	
	public List<FilterServer> filterList;
	public FilterServer filterCurrent;
	public int selected;
	
	public ContainerFilter(List<FilterServer> filterList, EntityPlayer player) {
		//this.inventorySlots = new ArrayListSlot();
		this.filterList = filterList;
		this.player = player;
	}
	
	public ContainerFilter setEntityId(int entityId) {
		this.entityId = entityId;
		this.mode = TargetType.ENTITY;
		return this;
	}
	
	public ContainerFilter setBlockPos(BlockPos pos) {
		this.pos = pos;
		this.mode = TargetType.BLOCK;
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
		slot.xPos += 62 / 2;
        slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slot);
        this.inventoryItemStacks.add(ItemStack.EMPTY);
        if(slot instanceof IPhantomSlot && ((IPhantomSlot)slot).canBeUnlimited())
        	PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), ((IPhantomSlot)slot).isUnlimited()), this.player);
        return slot;
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    	ItemStack stack = ItemStack.EMPTY;
    	if(filterCurrent != null && !(stack = filterCurrent.transferStackInSlot(this, par1EntityPlayer, par2)).isEmpty()) {
    		return stack;
    	}
    	else {
    		return ItemStack.EMPTY;
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
	public void putStackInSlot(int par1, ItemStack par2ItemStack) {
		Slot slot = this.getSlot(par1);
        if(slot != null) {
        	slot.putStack(par2ItemStack);
        }
	}

	//Phantom Slot
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		Slot slot = slotId < 0 || slotId >= inventorySlots.size() ? null : (Slot) this.inventorySlots.get(slotId);
		if (slot instanceof IPhantomSlot) {
			if(!this.getWorld().isRemote)
				return slotClickPhantom(slot, dragType, clickTypeIn, player);
			return ItemStack.EMPTY;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	
	private ItemStack slotClickPhantom(Slot slot, int mouseButton, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack stack = ItemStack.EMPTY;
		IPhantomSlot phantomSlot = (IPhantomSlot)slot;
		InventoryPlayer playerInv = player.inventory;
		ItemStack stackHeld = playerInv.getItemStack();
		ItemStack slotStack = slot.getStack();
		
		if(!stackHeld.isEmpty()) {
			ItemStack phantomStack = stackHeld.copy();
			phantomStack.setCount(!slotStack.isEmpty() ? slotStack.getCount() : 1);
			slot.putStack(phantomStack);
		}
		else if(!slotStack.isEmpty()) {
			
			if(mouseButton == 2) {
				slot.putStack(ItemStack.EMPTY);
				if(slot.inventory instanceof InventoryUnlimited)
					((InventoryUnlimited)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
				phantomSlot.setIsUnlimited(false);
				PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
			}
			else {
				int stackSize = mouseButton == 1 ? slotStack.getCount() + 1: slotStack.getCount() - 1;
			
				if(stackSize > 1 && phantomSlot.canBeUnlimited() && phantomSlot.isUnlimited()) {
					stackSize = 1;
					if(slot.inventory instanceof InventoryUnlimited)
						((InventoryUnlimited)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
					phantomSlot.setIsUnlimited(false);
					PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
				}
				else if(stackSize < 1 && phantomSlot.canBeUnlimited() && !phantomSlot.isUnlimited()) {
					stackSize = 1;
					if(slot.inventory instanceof InventoryUnlimited)
						((InventoryUnlimited)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), true);
					phantomSlot.setIsUnlimited(true);
					PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), true), player);
				}
				else if(stackSize < 1) {
					slot.putStack(ItemStack.EMPTY);
					if(slot.inventory instanceof InventoryUnlimited)
						((InventoryUnlimited)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
					phantomSlot.setIsUnlimited(false);
					PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
				}
				slotStack.setCount(stackSize);
			}
		}
		this.detectAndSendChanges();
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

	@Override
	public BlockPos getBlockPos() {
		return this.pos;
	}
	
	@Override
	public int getEntityId() {
		return this.entityId;
	}

	@Override
	public World getWorld() {
		return this.getPlayer().world;
	}

	@Override
	public FilterServer getCurrentFilter() {
		return this.filterCurrent;
	}

	@Override
	public Entity getEntity() {
		return this.getWorld().getEntityByID(this.getEntityId());
	}
}
