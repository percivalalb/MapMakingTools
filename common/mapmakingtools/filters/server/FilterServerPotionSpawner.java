package mapmakingtools.filters.server;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.inventory.SlotArmor;
import mapmakingtools.inventory.SlotFake;
import mapmakingtools.inventory.SlotFakeArmor;

public class FilterServerPotionSpawner implements IServerFilter {

	public static Map<String, PotionInventory> invMap = new Hashtable<String, PotionInventory>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String id = SpawnerHelper.getMobId(tile);
			if(id.equals("ThrownPotion")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

	@Override
	public void addSlots(ContainerFilter containerFilter) {
		int i;
        containerFilter.addSlotToContainer(new SlotFake(getInventory(containerFilter), 0, 23, 37) {
        	 @Override
        	 public boolean isItemValid(ItemStack stack) {
        		 if(stack != null && stack.getItem() instanceof ItemPotion) {
        			 return true;
        		 }
        		 return false;
        	 }
        });
		for (i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				containerFilter.addSlotToContainer(new Slot(containerFilter.player.inventory, j + i * 9 + 9, 59 + j * 18, 23 + i * 18));
	        }
		}	

	    for (i = 0; i < 9; ++i) {
	    	containerFilter.addSlotToContainer(new Slot(containerFilter.player.inventory, i, 59 + i * 18, 81));
	    }
	    containerFilter.putStackInSlot(0, SpawnerHelper.getPotionType(containerFilter.player.worldObj.getBlockTileEntity(containerFilter.x, containerFilter.y, containerFilter.z)));
	}
	

	@Override
	public ItemStack transferStackInSlot(ContainerFilter containerFilter, EntityPlayer par1EntityPlayer, int par2) {
		 ItemStack itemstack = null;
	        Slot slot = (Slot)containerFilter.inventorySlots.get(par2);

	        if (slot != null && slot.getHasStack()) {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();
	            boolean wasPhantomSlot = false;

	            if (itemstack.getItem() instanceof ItemPotion && !((Slot)containerFilter.inventorySlots.get(0)).getHasStack() && par2 >= 1 && par2 < 38) {
	            	wasPhantomSlot = true;
	                
	                if (!containerFilter.mergeItemStack(itemstack1, 0, 1, false)) {
	                    return null;
	                }
	            }
	            else if (par2 >= 28 && par2 < 37) {
	                if (!containerFilter.mergeItemStack(itemstack1, 1, 28, false)) {
	                    return null;
	                }
	            }
	            else if (!containerFilter.mergeItemStack(itemstack1, 28, 37, false))
	            {
	                return null;
	            }
	          
	            if(!wasPhantomSlot) {
	            	if(itemstack1.stackSize == 0) {
	            		slot.putStack((ItemStack)null);
	            	}
	            	else {
	            		slot.onSlotChanged();
	            	}
	            }
	            else {
	            	itemstack1.stackSize = itemstack.stackSize;
	            }
	            
	            if (itemstack1.stackSize == itemstack.stackSize)
	            {
	                return null;
	            }

	            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
	        }

	        return itemstack;
	}
	
	public PotionInventory getInventory(ContainerFilter player) {
		String username = PlayerHelper.usernameLowerCase(player.player);
	    if(!invMap.containsKey(player)) {
	    	invMap.put(username, new PotionInventory(1));
	    }
	    	
		return invMap.get(username);
	}

	public class PotionInventory implements IInventory {

		public ItemStack[] contents;
		
		public PotionInventory(int inventorySize) {
			this.contents = new ItemStack[inventorySize];
		}
		
		public int getSizeInventory() {
		    return this.contents.length;
		}

		public ItemStack getStackInSlot(int par1) {
		    return this.contents[par1];
		}

		public ItemStack decrStackSize(int par1, int par2) {
		     if (this.contents[par1] != null) {
		        ItemStack itemstack;

		        if (this.contents[par1].stackSize <= par2) {
		            itemstack = this.contents[par1];
		            this.contents[par1] = null;
		            return itemstack;
		        }
		        else {
		            itemstack = this.contents[par1].splitStack(par2);

		            if (this.contents[par1].stackSize == 0) {
		                this.contents[par1] = null;
		            }

		            return itemstack;
		        }
		    }
		    else {
		        return null;
		    }
		}

		public ItemStack getStackInSlotOnClosing(int par1) {
		    if (this.contents[par1] != null) {
		        ItemStack itemstack = this.contents[par1];
		        this.contents[par1] = null;
		        return itemstack;
		    }
		    else {
		        return null;
		    }
		}

		public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		    this.contents[par1] = par2ItemStack;

		    if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
		        par2ItemStack.stackSize = this.getInventoryStackLimit();
		    }
		}

		public String getInvName() {
		    return "Potion Spawner";
		}

		public boolean isInvNameLocalized() {
		    return true;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void onInventoryChanged() {
			//LogHelper.logDebug("On inventory changed");
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void openChest() {
			
		}

		@Override
		public void closeChest() {
			
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
	}
}
