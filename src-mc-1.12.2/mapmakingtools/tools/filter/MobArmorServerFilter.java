package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.container.InventoryUnlimited;
import mapmakingtools.container.SlotArmor;
import mapmakingtools.container.SlotFake;
import mapmakingtools.container.SlotFakeArmor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class MobArmorServerFilter extends FilterServer {

	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
	public static Map<String, InventoryUnlimited> invMap = new Hashtable<String, InventoryUnlimited>();
	
	@Override
	public void addSlots(IContainerFilter container) {
        for (int i = 0; i < 4; ++i) {
        	container.addSlot(new SlotArmor(container.getPlayer(), container.getPlayer().inventory, 36 + (3 - i), 130 + i * 18, 40, VALID_EQUIPMENT_SLOTS[i]));
        }
        container.addSlot(new SlotFake(getInventory(container), 0, 14, 39));
        container.addSlot(new SlotFake(getInventory(container), 1, 14, 66) {
            @SideOnly(Side.CLIENT)
            @Override
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });
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
	public ItemStack transferStackInSlot(IContainerFilter container, EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = (Slot)container.getInventorySlots().get(index);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
	        
	        boolean wasPhantomSlot = false;

	        if(entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !((Slot)container.getInventorySlots().get(9 - entityequipmentslot.getIndex())).getHasStack()) {
	        	wasPhantomSlot = true;
                int i = 9 - entityequipmentslot.getIndex();

                if (!container.mergeItemStacks(itemstack1, i, i + 1, false))
                    return ItemStack.EMPTY;
            }
	        else if(entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
                if (!container.mergeItemStacks(itemstack1, 4, 6, false))
                    return ItemStack.EMPTY;
	        }
	        
	        if(itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if(itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;
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
	
	public InventoryUnlimited getInventory(IContainerFilter containerFilter) {
		return this.getInventory(containerFilter.getPlayer().getName().toLowerCase());
	}
	
	public InventoryUnlimited getInventory(String username) {
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new InventoryUnlimited("Mob Armour", false, 6));
	    	
		return invMap.get(username);
	}
}
