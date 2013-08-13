package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.ChestSymmetrifyHelper;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketChestSymmetrify extends PacketMMT {

	public int x, y, z;
	
	public PacketChestSymmetrify() {
		super(PacketTypeHandler.CHEST_SYMMETRIFY, false);
	}
	
	public PacketChestSymmetrify(int x, int y, int z) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest)tile;
				int numberOfItems = 0;
				for(int index = 0; index < chest.getSizeInventory(); ++index) {
					if(chest.getStackInSlot(index) != null) ++numberOfItems;
				}
				List<ItemStack> stacksInChest = new ArrayList<ItemStack>();
				int currentCount = 0;
				for(int index = 0; index < chest.getSizeInventory(); ++index) {
					ItemStack stack = chest.getStackInSlot(index);
					if(stack != null) {
						stacksInChest.add(stack.copy());
						chest.setInventorySlotContents(index, null);
						++currentCount;
					}
				}
				
				List<ItemStack> sortedStacks = new ArrayList<ItemStack>();
				while(!stacksInChest.isEmpty()) {
					ItemStack stack = stacksInChest.get(0);
					Iterator<ItemStack> ite = stacksInChest.iterator();
					//int index = 0;
					while(ite.hasNext()) {
						ItemStack check = ite.next();
						if(check.itemID < stack.itemID) {
							stack = check;
							//++index;
						}
					}
					
					sortedStacks.add(stack.copy());
					LogHelper.logDebug("Add: " + stack.getDisplayName() + " Id: " + stack.itemID);
					//LogHelper.logDebug("Remove: " + stacksInChest.get(index).getDisplayName() + " Id: " + stacksInChest.get(index).itemID);
		
					stacksInChest.remove(stack);
					LogHelper.logDebug("List size: "+ stacksInChest.size());
				}
				
				for(ItemStack stack : sortedStacks) {
					if(stack != null) {
						LogHelper.logDebug(stack.getDisplayName() + " Id: " + stack.itemID);
					}
					else {
						LogHelper.logDebug("null Id: 0");
					}
				}
				
				String[][] pattern = ChestSymmetrifyHelper.getPattern(numberOfItems);
				
				int currentSlot = 0;
				
				int[] rows = new int[] {1, 0, 2};
				int[] columns = new int[] {4, 5, 3, 6, 2, 7, 1, 8, 0};
				for(int row : rows) {
					for(int column : columns) {
						int slot = row * 9 + column;
						if(pattern[row][column].equals("X")) {
							chest.setInventorySlotContents(slot, sortedStacks.get(currentSlot));
							++currentSlot;
						}
					}
				}
				
				LogHelper.logDebug("Number of Items in chest: " + numberOfItems);
				
				
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}
}
