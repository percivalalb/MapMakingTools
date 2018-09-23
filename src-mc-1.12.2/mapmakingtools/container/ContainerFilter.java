package mapmakingtools.container;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.api.interfaces.IContainerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

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
		this.filterList = filterList;
		this.player = player;
		//this.addSlot(new SlotFake(player.inventory, 0, 14, 39));
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
	    MapMakingTools.LOGGER.info(this.filterCurrent + " " + FMLCommonHandler.instance().getEffectiveSide());
	    if(this.filterCurrent != null)
	    	this.filterCurrent.addSlots(this);
	}
	
	@Override
	public void addSlot(Slot slot) {
		this.addSlotToContainer(slot);
	}
	
	@Override
	public Slot addSlotToContainer(Slot slotIn) {
		slotIn = super.addSlotToContainer(slotIn);
		slotIn.xPos += 62 / 2;
        return slotIn;
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

    	if(filterCurrent != null) {
    		return filterCurrent.transferStackInSlot(this, playerIn, index);
    	}
    	
    	return super.transferStackInSlot(playerIn, index);
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

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		Slot slot = slotId < 0 || slotId >= this.inventorySlots.size() ? null : (Slot) this.inventorySlots.get(slotId);
		
        if(slot instanceof IPhantomSlot) {
    		ItemStack slotStack = slot.getStack();
    		ItemStack playerStack = player.inventory.getItemStack();
        	
            if (playerStack.isEmpty()) {
            	
            	if(!slotStack.isEmpty()) {
        			if(clickTypeIn == ClickType.CLONE) { // Middle click
        				slot.putStack(ItemStack.EMPTY);
        			}
        			else if(clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) { //Left/Right click
        				int change = clickTypeIn == ClickType.QUICK_MOVE ? 8 : 1;
        				int size = slotStack.getCount() + (dragType == 1 ? change : -change);
        				size = Math.min(size, slot.getSlotStackLimit());
        				if(size < 1) {
        					slot.putStack(ItemStack.EMPTY);
        				}
        				else if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
        					slotStack.setCount(size);
        					this.detectAndSendChanges();
        				}
        			}
             	}
            } 
            else if(!playerStack.isEmpty()) {
    			ItemStack copy = playerStack.copy();
    			slot.putStack(copy);
    		}
            
            return playerStack;
        }
        
        return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	public static boolean canMerge(ItemStack a, ItemStack b) {
        return ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b);
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
	public TargetType getTargetType() {
		return this.mode;
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
	public Entity getEntity() {
		return this.getWorld().getEntityByID(this.getEntityId());
	}

	@Override
	public World getWorld() {
		return this.getPlayer().world;
	}

	@Override
	public FilterServer getCurrentFilter() {
		return this.filterCurrent;
	}
}
