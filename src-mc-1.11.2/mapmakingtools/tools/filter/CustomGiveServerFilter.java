package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.container.SlotFake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CustomGiveServerFilter extends IFilterServer {

	public static Map<String, CustomGive> invMap = new Hashtable<String, CustomGive>();
	
	@Override
	public void addSlots(IContainerFilter container) {
        container.addSlot(new SlotFake(getInventory(container), 0, 23, 37).setCantBeUnlimited());
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j) {
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 59 + j * 18, 23 + i * 18));
	        }
		}	

	    for (int i = 0; i < 9; ++i) {
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 59 + i * 18, 81));
	    }
	}
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityCommandBlock)
			return true;
		return super.isApplicable(player, world, pos);
	}
	
	@Override
	public String getSaveId() { 
		return "customGive"; 
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList list = (NBTTagList)tag.getTag("playerData");
		for(int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound data = list.getCompoundTagAt(i);
			String username = data.getString("username");
			boolean isUnlimited = data.getBoolean("isUnlimited");
			if(data.hasKey("item")) {
				ItemStack stack = new ItemStack(data.getCompoundTag("item"));
				this.getInventory(username).setInventorySlotContents(0, stack);
				this.getInventory(username).setSlotUnlimited(0, isUnlimited);
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { 
		NBTTagList list = new NBTTagList();
		for(String key : invMap.keySet()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("username", key);
			data.setBoolean("isUnlimited", this.getInventory(key).isSlotUnlimited(0));
			if(!this.getInventory(key).getStackInSlot(0).isEmpty())
				data.setTag("item", this.getInventory(key).getStackInSlot(0).writeToNBT(new NBTTagCompound()));
			list.appendTag(data);
		}
		tag.setTag("playerData", list);
		return tag; 
	}
	
	public CustomGive getInventory(IContainerFilter containerFilter) {
		String username = containerFilter.getPlayer().getName().toLowerCase();
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new CustomGive(1));
	    	
		return invMap.get(username);
	}
	
	public CustomGive getInventory(String username) {
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new CustomGive(1));
	    	
		return invMap.get(username);
	}

	public class CustomGive extends IUnlimitedInventory {

		public boolean[] umlimited;
		
		public CustomGive(int inventorySize) {
			super("Custom Give", false, inventorySize);
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
