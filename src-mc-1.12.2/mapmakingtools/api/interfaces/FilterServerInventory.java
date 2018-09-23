package mapmakingtools.api.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class FilterServerInventory extends FilterServer {

	public static Map<UUID, IInventory> INV_MAP = new HashMap<UUID, IInventory>();
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		
		if(tag.hasKey("inventories", NBTUtil.ID_LIST)) {
		
			NBTTagList inventoriesData = (NBTTagList)tag.getTagList("inventories", NBTUtil.ID_COMPOUND);
			for(int i = 0; i < inventoriesData.tagCount(); ++i) {
				NBTTagCompound inventoryData = inventoriesData.getCompoundTagAt(i);
				IInventory inventory = this.getInventory(inventoryData.getUniqueId("uuid"));
				
				NBTTagList itemData = inventoryData.getTagList("Items", NBTUtil.ID_COMPOUND);

		        for(int j = 0; j < itemData.tagCount(); ++j) {
		            NBTTagCompound nbttagcompound = itemData.getCompoundTagAt(j);
		            int k = nbttagcompound.getByte("Slot") & 255;

		            if(k >= 0 && k < inventory.getSizeInventory()) {
		            	inventory.setInventorySlotContents(k, new ItemStack(nbttagcompound));
		            }
		        }
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { 
		
		NBTTagList inventoriesData = new NBTTagList();
		for(UUID key : INV_MAP.keySet()) {
			IInventory inventory = this.getInventory(key);
			
			NBTTagCompound inventoryData = new NBTTagCompound();
			inventoryData.setUniqueId("uuid", key);
			
			NBTTagList itemData = new NBTTagList();
	        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
	            ItemStack itemstack = inventory.getStackInSlot(i);

	            if(!itemstack.isEmpty()) {
	                NBTTagCompound nbttagcompound = new NBTTagCompound();
	                nbttagcompound.setByte("Slot", (byte)i);
	                itemstack.writeToNBT(nbttagcompound);
	                itemData.appendTag(nbttagcompound);
	            }
	        }

	        if(!itemData.hasNoTags()) {
	        	inventoryData.setTag("Items", itemData);
	        	inventoriesData.appendTag(inventoryData);
	        }
		}
		tag.setTag("inventories", inventoriesData);
		
		return tag; 
	}
	
	public IInventory getInventory(IContainerFilter container) {
		return getInventory(container.getPlayer());
	}
	
	public IInventory getInventory(EntityPlayer player) {
		return getInventory(player.getUniqueID());
	}
	
	public IInventory getInventory(UUID uuid) {
	    if(!INV_MAP.containsKey(uuid))
	    	INV_MAP.put(uuid, createInventory());
	    	
		return INV_MAP.get(uuid);
	}
	
	public abstract IInventory createInventory();
}
