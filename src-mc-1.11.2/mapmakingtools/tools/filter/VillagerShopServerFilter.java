package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.container.InventoryUnlimited;
import mapmakingtools.container.SlotFake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/**
 * @author ProPercivalalb
 */
public class VillagerShopServerFilter extends FilterServer {

	public static Map<UUID, Integer> maxRecipesMap = new Hashtable<UUID, Integer>();
	public static Map<UUID, Integer> tradeCountMap = new Hashtable<UUID, Integer>();
	public static Map<UUID, VillagerShopInventory> invMap = new Hashtable<UUID, VillagerShopInventory>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}
	
	@Override
	public void addSlots(IContainerFilter container) {
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
	
	public void addOnlySlots(IContainerFilter container) {
		container.getInventorySlots().clear();
		
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 40 + j * 18, 109 + i * 18));
		
		for (int i = 0; i < 9; ++i)
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 40 + i * 18, 167));

	    for (int i = 0; i < this.getAmountRecipes(container.getPlayer()) && i < 9; ++i) {
	    	container.addSlot(new SlotFake(this.getInventory(container), i * 3, 	20 + (i * 18) + (i * 5), 24).setCantBeUnlimited());
	    	container.addSlot(new SlotFake(this.getInventory(container), i * 3 + 1, 20 + (i * 18) + (i * 5), 42).setCantBeUnlimited());
	    	container.addSlot(new SlotFake(this.getInventory(container), i * 3 + 2, 20 + (i * 18) + (i * 5), 64).setCantBeUnlimited());
	    }
		
		EntityPlayer player = container.getPlayer();
		Entity entity = container.getEntity();
			
		if(entity instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager)entity;
		    MerchantRecipeList recipeList = villager.getRecipes(player);
		    for (int i = 0; i < this.getAmountRecipes(player) && i < 9 && i < recipeList.size(); ++i) {
		    MerchantRecipe recipes = (MerchantRecipe)recipeList.get(i);
		       	this.getInventory(player).setInventorySlotContents(i * 3, recipes.getItemToBuy());
		        this.getInventory(player).setInventorySlotContents(i * 3 + 1, recipes.getSecondItemToBuy());
		        this.getInventory(player).setInventorySlotContents(i * 3 + 2, recipes.getItemToSell());
		    }
		}
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
		for(UUID uuid : invMap.keySet()) {
			
		}
		tag.setTag("playerData", list);
		return tag; 
	}
	
	public int getAmountRecipes(IContainerFilter container) {
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
	
	public VillagerShopInventory getInventory(IContainerFilter container) {
		return getInventory(container.getPlayer());
	}
	
	public VillagerShopInventory getInventory(EntityPlayer player) {
		return getInventory(player.getUniqueID());
	}
	
	public VillagerShopInventory getInventory(UUID uuid) {
	    if(!invMap.containsKey(uuid))
	    	invMap.put(uuid, new VillagerShopInventory(256 * 3));
	    	
		return invMap.get(uuid);
	}

	public class VillagerShopInventory extends InventoryBasic {
		
		public VillagerShopInventory(int inventorySize) {
			super("Villager Shop", false, inventorySize);
		}
	}
}
