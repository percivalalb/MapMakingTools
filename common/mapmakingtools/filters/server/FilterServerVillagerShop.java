package mapmakingtools.filters.server;

import java.util.Hashtable;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.inventory.SlotFake;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketMaxVillagerRecipes;

public class FilterServerVillagerShop implements IServerFilter {

	public static Map<String, Integer> maxRecipesMap = new Hashtable<String, Integer>();
	public static Map<String, Integer> tradeCountMap = new Hashtable<String, Integer>();
	public static Map<String, VillagerShopInventory> invMap = new Hashtable<String, VillagerShopInventory>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		if(entity != null && entity instanceof EntityVillager) {
			return true;
		}
		return false;
	}

	@Override
	public void addSlots(ContainerFilter containerFilter) {
        /**Checks how many recipes there currently in the villager**/
		if(containerFilter.entityId != 0) {
        	World world = containerFilter.player.worldObj;
        	Entity entity = world.getEntityByID(containerFilter.entityId);
        	if(entity != null && entity instanceof EntityVillager) {
        		EntityVillager villager = (EntityVillager)entity;
        		MerchantRecipeList recipeList = villager.getRecipes(containerFilter.player);
        		if(recipeList != null) {
        			maxRecipesMap.put(PlayerHelper.usernameLowerCase(containerFilter.player), recipeList.size());
        		}
        		else {
        			maxRecipesMap.put(PlayerHelper.usernameLowerCase(containerFilter.player), 1);
        		}
        	}
        }
		int i;
		addOnlySlots(containerFilter);
		
		if(containerFilter.entityId != 0) {
	        World world = containerFilter.player.worldObj;
	        Entity entity = world.getEntityByID(containerFilter.entityId);
	        if(entity != null && entity instanceof EntityVillager) {
	        	EntityVillager villager = (EntityVillager)entity;
	        	MerchantRecipeList recipeList = villager.getRecipes(containerFilter.player);
	    	    for (i = 0; i < getAmountRecipes(containerFilter.player) && i < 9; ++i) {
	    	    	MerchantRecipe recipes = (MerchantRecipe)recipeList.get(i);
	    	    	containerFilter.getSlot(i * 3).putStack(recipes.getItemToBuy());
	    	    	containerFilter.getSlot(i * 3 + 1).putStack(recipes.getSecondItemToBuy());
	    	    	containerFilter.getSlot(i * 3 + 2).putStack(recipes.getItemToSell());
	    	    }
	        }
	    }
		
	    LogHelper.logDebug("Max Recipes: " + maxRecipesMap.get(PlayerHelper.usernameLowerCase(containerFilter.player)));
	}
	
	public void addOnlySlots(ContainerFilter containerFilter) {
		ItemStack[] stacks = new ItemStack[256 * 3];
		int slots = 0;
		int i;
		for (i = 0; i < getAmountRecipes(containerFilter.player) * 3; ++i) {
			Slot slot = containerFilter.getSlot(i);
			if(slot != null && slot.inventory instanceof VillagerShopInventory) {
				slots++;
				ItemStack stack = slot.getStack();
				if(stack != null) {
					stacks[i] = stack.copy();
				}
			}
		}
		
		containerFilter.inventorySlots.clear();
		containerFilter.inventoryItemStacks.clear();
	    for (i = 0; i < getAmountRecipes(containerFilter.player) && i < 9; ++i) {
	    	containerFilter.addSlotToContainer(new SlotFake(this.getInventory(containerFilter), i * 3, 	   20 + (i * 18) + (i * 5), 24));
	    	containerFilter.addSlotToContainer(new SlotFake(this.getInventory(containerFilter), i * 3 + 1, 20 + (i * 18) + (i * 5), 42));
	    	containerFilter.addSlotToContainer(new SlotFake(this.getInventory(containerFilter), i * 3 + 2, 20 + (i * 18) + (i * 5), 64));
	    }
		
		for (i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				containerFilter.addSlotToContainer(new Slot(containerFilter.player.inventory, j + i * 9 + 9, 40 + j * 18, 109 + i * 18));
	        }
		}
		
		for (i = 0; i < 9; ++i) {
	    	containerFilter.addSlotToContainer(new Slot(containerFilter.player.inventory, i, 40 + i * 18, 167));
	    }
		
		for(i = 0; i < stacks.length && i < slots; ++i) {
	    	containerFilter.getSlot(i).putStack(stacks[i]);
		}
	}

	@Override
	public ItemStack transferStackInSlot(ContainerFilter containerFilter, EntityPlayer par1EntityPlayer, int par2) {
		ItemStack itemstack = null;
	    Slot slot = (Slot)containerFilter.inventorySlots.get(par2);
	    int shopSlots = getAmountRecipes(containerFilter.player) * 3;
	    
	    if(slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	         itemstack = itemstack1.copy();
	         boolean wasPhantomSlot = false;

	         if (par2 >= shopSlots + 1 && par2 < shopSlots + 37) {
	        	wasPhantomSlot = true;
	                
	            if (!containerFilter.mergeItemStack(itemstack1, 0, shopSlots, false)) {
	                return null;
	            }
	        }
	        else if (!containerFilter.mergeItemStack(itemstack1, 36, 45, false)) {
		        return null;
		    }
	        else if (!containerFilter.mergeItemStack(itemstack1, 36, 45, false)) {
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
	
	public int getAmountRecipes(EntityPlayer player) {
		int amountRecipes = 0;
		if(maxRecipesMap.containsKey(PlayerHelper.usernameLowerCase(player))) {
			amountRecipes = maxRecipesMap.get(PlayerHelper.usernameLowerCase(player));
		}
		return amountRecipes;
	}
	
	public VillagerShopInventory getInventory(ContainerFilter player) {
		String username = PlayerHelper.usernameLowerCase(player.player);
	    if(!invMap.containsKey(player)) {
	    	invMap.put(username, new VillagerShopInventory(256 * 3));
	    }
	    	
		return invMap.get(username);
	}

	public class VillagerShopInventory implements IInventory {

		public ItemStack[] contents;
		
		public VillagerShopInventory(int inventorySize) {
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
		    return "Villager Shop";
		}

		public boolean isInvNameLocalized() {
		    return true;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
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
