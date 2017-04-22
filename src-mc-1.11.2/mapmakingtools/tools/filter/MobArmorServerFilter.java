package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.container.SlotArmor;
import mapmakingtools.container.SlotFake;
import mapmakingtools.container.SlotFakeArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class MobArmorServerFilter extends IFilterServer {

	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	public static Map<String, MobArmor> invMap = new Hashtable<String, MobArmor>();
	
	@Override
	public void addSlots(IContainerFilter container) {
        for (int i = 0; i < 4; ++i) {
        	container.addSlot(new SlotArmor(container.getPlayer(), container.getPlayer().inventory, container.getPlayer().inventory.getSizeInventory() - 1 - i, 130 + i * 18, 40, VALID_EQUIPMENT_SLOTS[i]));
        }
        container.addSlot(new SlotFake(getInventory(container), 0, 14, 39));
        for (int i = 0; i < 4; ++i) {
        	container.addSlot(new SlotFakeArmor(container.getPlayer(), getInventory(container), getInventory(container).getSizeInventory() - 1 - i, 40 + i * 18, 40, VALID_EQUIPMENT_SLOTS[i]));
        }
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 40 + j * 18, 70 + i * 18));
	        }
		}	

	    for (int i = 0; i < 9; ++i) {
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 40 + i * 18, 128));
	    }
	}
	
	@Override
	public ItemStack transferStackInSlot(IContainerFilter container, EntityPlayer par1EntityPlayer, int par2) {
		ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = (Slot)container.getInventorySlots().get(par2);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	        boolean wasPhantomSlot = false;

	        if (itemstack.getItem() instanceof ItemArmor && !((Slot)container.getInventorySlots().get(5 + ((ItemArmor)itemstack.getItem()).armorType.getIndex())).getHasStack() && (par2 >= 9 && par2 < 45 || par2 >= 0 && par2 < 4)) {
	        	wasPhantomSlot = true;
	        	int j = 5 + ((ItemArmor)itemstack.getItem()).armorType.getIndex();
	                
	            if (!container.mergeItemStacks(itemstack1, j, j + 1, false))
	                return ItemStack.EMPTY;
	        }
	        else if(par2 >= 9 && par2 < 45 && !(itemstack.getItem() instanceof ItemArmor) && !((Slot)container.getInventorySlots().get(4)).getHasStack()) {
	        	wasPhantomSlot = true;
	        	if (!container.mergeItemStacks(itemstack1, 4, 4 + 1, false))
	                return ItemStack.EMPTY;
	        }
	        else if (par2 >= 36 && par2 < 45) {
	            if (!container.mergeItemStacks(itemstack1, 9, 36, false))
	                return ItemStack.EMPTY;
	        }
	        else if (!container.mergeItemStacks(itemstack1, 36, 45, false))
	            return ItemStack.EMPTY;
	          
	        if(!wasPhantomSlot) {
	        	if(itemstack1.isEmpty())
	        		slot.putStack(ItemStack.EMPTY);
	            else
	            	slot.onSlotChanged();
	        }
	        else
	        	itemstack1.setCount(itemstack.getCount());
	            
	        if (itemstack1.getCount() == itemstack.getCount())
	            return null;

	        //slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
	    }

	    return itemstack;
	}
	
	@Override
	public String getSaveId() {
		return "mobArmor";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList list = (NBTTagList)tag.getTag("playerData");
		for(int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound data = list.getCompoundTagAt(i);
			String username = data.getString("username");
			NBTTagList nbttaglist = (NBTTagList)data.getTag("items");

	        for (int k = 0; k < nbttaglist.tagCount(); ++k) {
	            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(k);

	            this.getInventory(username).setInventorySlotContents(k, new ItemStack(nbttagcompound1.getCompoundTag("item")));
	            this.getInventory(username).umlimited[k] = nbttagcompound1.getBoolean("isUnlimited");
	        }
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { 
		NBTTagList list = new NBTTagList();
		for(String key : invMap.keySet()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("username", key);
			
			NBTTagList nbttaglist = new NBTTagList();
			for(int i = 0; i < this.getInventory(key).getSizeInventory(); ++i) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setBoolean("isUnlimited", this.getInventory(key).isSlotUnlimited(i));
				data.setTag("item", this.getInventory(key).getStackInSlot(i).writeToNBT(new NBTTagCompound()));

				nbttaglist.appendTag(nbttagcompound1);
			}
			data.setTag("items", nbttaglist);
			list.appendTag(data);
		}
		tag.setTag("playerData", list);
		return tag; 
	}
	
	public MobArmor getInventory(IContainerFilter container) {
		String username = container.getPlayer().getName().toLowerCase();
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new MobArmor(5));
	    	
		return invMap.get(username);
	}
	
	public MobArmor getInventory(String username) {
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new MobArmor(5));
	    	
		return invMap.get(username);
	}

	public class MobArmor extends IUnlimitedInventory {

		public boolean[] umlimited;
		
		public MobArmor(int inventorySize) {
			super("Mob Armour", false, inventorySize);
			this.umlimited = new boolean[inventorySize];
		}
		
		@Override
		public boolean isSlotUnlimited(int slotIndex) {
			return this.umlimited[slotIndex];
		}
		
		@Override
		public void setSlotUnlimited(int slotIndex, boolean isUnlimited) {
			this.umlimited[slotIndex] = isUnlimited;
		}
	}
}
