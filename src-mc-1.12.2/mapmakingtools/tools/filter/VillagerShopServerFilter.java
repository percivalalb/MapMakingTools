package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import mapmakingtools.api.filter.FilterServerInventory;
import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.helper.SideHelper;
import mapmakingtools.inventory.slot.SlotFake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/**
 * @author ProPercivalalb
 */
public class VillagerShopServerFilter extends FilterServerInventory {

	public static Map<UUID, Integer> maxRecipesMap = new Hashtable<UUID, Integer>();
	public static Map<UUID, Integer> tradeCountMap = new Hashtable<UUID, Integer>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}
	
	@Override
	public void addSlots(IFilterContainer container) {
		EntityPlayer player = container.getPlayer();
		
		Entity entity = container.getEntity();
		
        if(entity instanceof EntityVillager) {
        	EntityVillager villager = (EntityVillager)entity;
        	MerchantRecipeList recipeList = villager.getRecipes(player);
        	if(recipeList != null)
        		maxRecipesMap.put(player.getUniqueID(), recipeList.size());
        	else
        		maxRecipesMap.put(player.getUniqueID(), 1);
        }
		this.addOnlySlots(container);
	}
	
	public void addOnlySlots(IFilterContainer container) {
		EntityPlayer player = container.getPlayer();
		IInventory inventory = this.getInventory(container);
		
		container.getInventorySlots().clear();
		
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				container.addSlot(new Slot(player.inventory, j + i * 9 + 9, 40 + j * 18, 109 + i * 18));
		
		for (int i = 0; i < 9; ++i)
	    	container.addSlot(new Slot(player.inventory, i, 40 + i * 18, 167));

	    for (int i = 0; i < this.getAmountRecipes(player) && i < 9; ++i) {
	    	container.addSlot(new SlotFake(inventory, i * 3, 	20 + (i * 18) + (i * 5), 24));
	    	container.addSlot(new SlotFake(inventory, i * 3 + 1, 20 + (i * 18) + (i * 5), 42));
	    	container.addSlot(new SlotFake(inventory, i * 3 + 2, 20 + (i * 18) + (i * 5), 64));
	    }
		
	    if(SideHelper.isServer()) {
			Entity entity = container.getEntity();
			
			if(entity instanceof EntityVillager) {
				EntityVillager villager = (EntityVillager)entity;
			    MerchantRecipeList recipeList = villager.getRecipes(player);
			    for (int i = 0; i < this.getAmountRecipes(player) && i < 9 && i < recipeList.size(); ++i) {
			    	MerchantRecipe recipes = (MerchantRecipe)recipeList.get(i);
			    	inventory.setInventorySlotContents(i * 3, recipes.getItemToBuy());
			    	inventory.setInventorySlotContents(i * 3 + 1, recipes.getSecondItemToBuy());
			        inventory.setInventorySlotContents(i * 3 + 2, recipes.getItemToSell());
			    }
			}
	    }
	}

	@Override
	public String getSaveId() { 
		return "villagerShop"; 
	}
	
	@Override
	public IInventory createInventory() {
		return new InventoryBasic("Villager Shop", false, 45);
	}
	
	public int getAmountRecipes(IFilterContainer container) {
		return getAmountRecipes(container.getPlayer());
	}
	
	public int getAmountRecipes(EntityPlayer player) {
		return getAmountRecipes(player.getUniqueID());
	}
	
	public int getAmountRecipes(UUID uuid) {
		if(!maxRecipesMap.containsKey(uuid))
			return 0;
		return maxRecipesMap.get(uuid);
	}
}
