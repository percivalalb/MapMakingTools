package mapmakingtools.api.filter;

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

	public Map<UUID, IInventory> INV_MAP = new HashMap<UUID, IInventory>();
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		
		if(tag.contains("inventories", NBTUtil.ID_LIST)) {
		
			NBTTagList inventoriesData = (NBTTagList)tag.getList("inventories", NBTUtil.ID_COMPOUND);
			for(int i = 0; i < inventoriesData.size(); ++i) {
				NBTTagCompound inventoryData = inventoriesData.getCompound(i);
				IInventory inventory = this.getInventory(inventoryData.getUniqueId("uuid"));
				
				NBTTagList itemData = inventoryData.getList("Items", NBTUtil.ID_COMPOUND);

		        for(int j = 0; j < itemData.size(); ++j) {
		            NBTTagCompound nbttagcompound = itemData.getCompound(j);
		            int k = nbttagcompound.getByte("Slot") & 255;

		            if(k >= 0 && k < inventory.getSizeInventory()) {
		            	inventory.setInventorySlotContents(k, ItemStack.read(nbttagcompound));
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
			inventoryData.putUniqueId("uuid", key);
			
			NBTTagList itemData = new NBTTagList();
	        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
	            ItemStack itemstack = inventory.getStackInSlot(i);

	            if(!itemstack.isEmpty()) {
	                NBTTagCompound nbttagcompound = new NBTTagCompound();
	                nbttagcompound.putByte("Slot", (byte)i);
	                itemstack.write(nbttagcompound);
	                itemData.add(nbttagcompound);
	            }
	        }

	        if(!itemData.isEmpty()) {
	        	inventoryData.put("Items", itemData);
	        	inventoriesData.add(inventoryData);
	        }
		}
		tag.put("inventories", inventoriesData);
		
		return tag; 
	}
	
	public IInventory getInventory(IFilterContainer container) {
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
