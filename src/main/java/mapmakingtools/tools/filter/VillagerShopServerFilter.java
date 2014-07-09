package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.container.SlotFake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class VillagerShopServerFilter extends IFilterServer {

	public static Map<String, Integer> maxRecipesMap = new Hashtable<String, Integer>();
	public static Map<String, Integer> tradeCountMap = new Hashtable<String, Integer>();
	public static Map<String, VillagerShopInventory> invMap = new Hashtable<String, VillagerShopInventory>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}
	
	@Override
	public void addSlots(IContainerFilter container) {
		EntityPlayer player = container.getPlayer();
		String username = container.getPlayer().getCommandSenderName();
		World world = container.getWorld();
		Entity entity = world.getEntityByID(container.getEntityId());
		
        if(entity instanceof EntityVillager) {
        	EntityVillager villager = (EntityVillager)entity;
        	MerchantRecipeList recipeList = villager.getRecipes(player);
        	if(recipeList != null)
        		maxRecipesMap.put(username, recipeList.size());
        	else
        		maxRecipesMap.put(username, 1);
        }
		this.addOnlySlots(container);
		
	    if(entity instanceof EntityVillager) {
	       	EntityVillager villager = (EntityVillager)entity;
	       	MerchantRecipeList recipeList = villager.getRecipes(player);
	        for (int i = 0; i < this.getAmountRecipes(player) && i < 9; ++i) {
	        	MerchantRecipe recipes = (MerchantRecipe)recipeList.get(i);
	        	this.getInventory(username).setInventorySlotContents(i * 3, recipes.getItemToBuy());
	        	this.getInventory(username).setInventorySlotContents(i * 3 + 1, recipes.getSecondItemToBuy());
	        	this.getInventory(username).setInventorySlotContents(i * 3 + 2, recipes.getItemToSell());
	        }
	    }
	}
	
	public void addOnlySlots(IContainerFilter container) {
		container.getInventorySlots().clear();
	    for (int i = 0; i < this.getAmountRecipes(container.getPlayer()) && i < 9; ++i) {
	    	container.addSlot(new SlotFake(this.getInventory(container), i * 3, 	20 + (i * 18) + (i * 5), 24).setCantBeUnlimited());
	    	container.addSlot(new SlotFake(this.getInventory(container), i * 3 + 1, 20 + (i * 18) + (i * 5), 42).setCantBeUnlimited());
	    	container.addSlot(new SlotFake(this.getInventory(container), i * 3 + 2, 20 + (i * 18) + (i * 5), 64).setCantBeUnlimited());
	    }
		
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 40 + j * 18, 109 + i * 18));
		
		for (int i = 0; i < 9; ++i)
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 40 + i * 18, 167));
	
	}

	@Override
	public String getSaveId() { 
		return "villagerShop"; 
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList list = (NBTTagList)tag.getTag("playerData");
		for(int i = 0; i < list.tagCount(); ++i) {
			
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { 
		NBTTagList list = new NBTTagList();
		for(String key : invMap.keySet()) {
			
		}
		tag.setTag("playerData", list);
		return tag; 
	}
	
	
	public int getAmountRecipes(IContainerFilter container) {
		return getAmountRecipes(container.getPlayer());
	}
	
	public int getAmountRecipes(EntityPlayer player) {
		return getAmountRecipes(player.getCommandSenderName());
	}
	
	public int getAmountRecipes(String username) {
		if(!maxRecipesMap.containsKey(username))
			return 0;
		return maxRecipesMap.get(username);
	}
	
	public VillagerShopInventory getInventory(IContainerFilter container) {
		return getInventory(container.getPlayer());
	}
	
	public VillagerShopInventory getInventory(EntityPlayer player) {
		return getInventory(player.getCommandSenderName());
	}
	
	public VillagerShopInventory getInventory(String username) {
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new VillagerShopInventory(256 * 3));
	    	
		return invMap.get(username);
	}

	public class VillagerShopInventory implements IUnlimitedInventory {

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

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}

		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}
		
		@Override
		public String getInventoryName() {
			return "Item Spawner";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}
		
		@Override
		public boolean isSlotUnlimited(int slotIndex) {
			return false;
		}
		
		@Override
		public void setSlotUnlimited(int slotIndex, boolean isUnlimited) {
			
		}
	}
}
