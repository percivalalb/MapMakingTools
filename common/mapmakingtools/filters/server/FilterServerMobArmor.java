package mapmakingtools.filters.server;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
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

public class FilterServerMobArmor implements IServerFilter {

	public static Map<String, MobArmorInventory> invMap = new Hashtable<String, MobArmorInventory>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String id = SpawnerHelper.getMobId(tile);
			if(id.equals("Zombie") || id.equals("PigZombie") || id.equals("Skeleton")) {
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
        for (i = 0; i < 4; ++i) {
        	containerFilter.addSlotToContainer(new SlotArmor(containerFilter.player, containerFilter.player.inventory, containerFilter.player.inventory.getSizeInventory() - 1 - i, 130 + i * 18, 40, i));
        }
        containerFilter.addSlotToContainer(new SlotFake(getInventory(containerFilter), 0, 14, 39));
        for (i = 0; i < 4; ++i) {
        	containerFilter.addSlotToContainer(new SlotFakeArmor(containerFilter.player, getInventory(containerFilter), getInventory(containerFilter).getSizeInventory() - 1 - i, 40 + i * 18, 40, i));
        }
		for (i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				containerFilter.addSlotToContainer(new Slot(containerFilter.player.inventory, j + i * 9 + 9, 40 + j * 18, 70 + i * 18));
	        }
		}	

	    for (i = 0; i < 9; ++i) {
	    	containerFilter.addSlotToContainer(new Slot(containerFilter.player.inventory, i, 40 + i * 18, 128));
	    }
	    ItemStack[] armor = SpawnerHelper.getMobArmor(containerFilter.player.worldObj.getBlockTileEntity(containerFilter.x, containerFilter.y, containerFilter.z));
	    for(i = 0; i < armor.length; ++i) {
	    	containerFilter.putStackInSlot(i + 4, armor[(i == 0 ? 0 : 5 - i)]);
	    }
	}
	

	@Override
	public ItemStack transferStackInSlot(ContainerFilter containerFilter, EntityPlayer par1EntityPlayer, int par2) {
		 ItemStack itemstack = null;
	        Slot slot = (Slot)containerFilter.inventorySlots.get(par2);

	        if (slot != null && slot.getHasStack()) {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();
	            boolean wasPhantomSlot = false;

	            if (itemstack.getItem() instanceof ItemArmor && !((Slot)containerFilter.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack() && (par2 >= 9 && par2 < 45 || par2 >= 0 && par2 < 4)) {
	            	wasPhantomSlot = true;
	            	int j = 5 + ((ItemArmor)itemstack.getItem()).armorType;
	                
	                if (!containerFilter.mergeItemStack(itemstack1, j, j + 1, false)) {
	                    return null;
	                }
	            }
	            else if(par2 >= 9 && par2 < 45 && !(itemstack.getItem() instanceof ItemArmor) && !((Slot)containerFilter.inventorySlots.get(4)).getHasStack()) {
	            	wasPhantomSlot = true;
	            	if (!containerFilter.mergeItemStack(itemstack1, 4, 4 + 1, false)) {
	                    return null;
	                }
	            }
	            else if (par2 >= 36 && par2 < 45) {
	                if (!containerFilter.mergeItemStack(itemstack1, 9, 36, false)) {
	                    return null;
	                }
	            }
	            else if (!containerFilter.mergeItemStack(itemstack1, 36, 45, false))
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
	
	public MobArmorInventory getInventory(ContainerFilter player) {
		String username = PlayerHelper.usernameLowerCase(player.player);
	    if(!invMap.containsKey(player)) {
	    	invMap.put(username, new MobArmorInventory(5));
	    }
	    	
		return invMap.get(username);
	}

	public class MobArmorInventory implements IInventory {

		public ItemStack[] contents;
		
		public MobArmorInventory(int inventorySize) {
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
		    return "Mob Armor";
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
