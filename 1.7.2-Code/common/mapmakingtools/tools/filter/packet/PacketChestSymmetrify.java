package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.datareader.ChestSymmetrifyData;
import mapmakingtools.tools.filter.CustomGiveServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class PacketChestSymmetrify extends IPacket {

	public int x, y, z;
	
	public PacketChestSymmetrify() {}
	public PacketChestSymmetrify(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(tile instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest)tile;
			
			//Gets the valid stacks from the chest and stores them in a list
			List<ItemStack> stacksInChest = new ArrayList<ItemStack>();
			int currentCount = 0;
			for(int index = 0; index < chest.getSizeInventory(); ++index) {
				ItemStack stack = chest.getStackInSlot(index);
				if(stack != null) {
					stacksInChest.add(stack);
					chest.setInventorySlotContents(index, null);
					++currentCount;
				}
			}
			
			if(stacksInChest.size() < 1) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.chestsymmetrify.nocontents");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
			}	
			
			//Counts the amount of each id in the chest
			Map<Integer, Integer> idCounts = new HashMap<Integer, Integer>();
			for(ItemStack stack : stacksInChest) {
				int id = Item.getIdFromItem(stack.getItem());
				if(idCounts.containsKey(id)) {
					idCounts.put(id, idCounts.get(id) + 1);
				}
				else {
					idCounts.put(id, 1);
				}
			}
			
			List<ArrayList<ItemStack>> sortedItems = new ArrayList<ArrayList<ItemStack>>();
			
			while(idCounts.size() > 0) {
				int maxEvenId = 0;
				int maxEvenCount = 0;
				int maxOddId = 0;
				int maxOddCount = 0;
				
				Iterator<Integer> ite = idCounts.keySet().iterator();
				
				while(ite.hasNext()) {
					int id = ite.next();
					if(idCounts.get(id) % 2 == 0 && idCounts.get(id) > maxEvenCount) {
						maxEvenId = id;
						maxEvenCount = idCounts.get(id);
					}
					if(idCounts.get(id) % 2 == 1 && idCounts.get(id) > maxOddCount) {
						maxOddId = id;
						maxOddCount = idCounts.get(id);
					}
				}
				
				int maxId;	
				if(maxEvenCount != 0) {
					maxId = maxEvenId;
				}
				else {
					maxId = maxOddId;
				}
				
				ArrayList<ItemStack> group = new ArrayList<ItemStack>();
				for(ItemStack stack : stacksInChest) {
					if(Item.getIdFromItem(stack.getItem()) == maxId) {
						group.add(stack);
					}
				}
				
				sortedItems.add(group);
				idCounts.remove(maxId);	
			}
			
			int[] taken = new int[27];
			//Arrays.fill(taken, 0);
			
			String[][] arrangement = ChestSymmetrifyData.getPattern(stacksInChest.size());
			
			Map<Integer, ItemStack> newItems = new HashMap<Integer, ItemStack>();
			
			for(int row : new int[] {1, 0, 2}) {
				if(arrangement[row][4].equalsIgnoreCase("x")) {
					for(ArrayList<ItemStack> group : sortedItems) {
						if(group.size() % 2 == 1) {
							int qtyPlaced = 0;
							for(ItemStack item : group) {
								boolean placed = false;
								for(int column : new int[] {4, 5, 3, 6, 2, 7, 1, 8, 0}) {
									int idx = row * 9 + column;
									if(arrangement[row][column].equalsIgnoreCase("x") && taken[idx] == 0) {
										taken[idx] = 1;
										newItems.put(idx, item);
										placed = true;
										qtyPlaced += 1;
										break;
									}
								}
								if(!placed) {
									break;
								}
							}
							for(int i = 0; i < qtyPlaced; ++i) {
								group.remove(group.get(0));
							}
							break;
							
						}
					}
				}
			}
			
			for(ArrayList<ItemStack> group : sortedItems) {
				for(ItemStack item : group) {
					boolean placed = false;
					for(int row : new int[] {1, 0, 2}) {
						for(int column : new int[] {4, 5, 3, 6, 2, 7, 1, 8, 0}) {
							int idx = row * 9 + column;
							if(arrangement[row][column].equalsIgnoreCase("x") && taken[idx] == 0) {
								taken[idx] = 1;
								newItems.put(idx, item);
								placed = true;
								break;
							}
						}
						
						if(placed) {
							break;
						}
							
					}
				}
			}
			
			for(int index = 0; index < chest.getSizeInventory(); ++index) {
				chest.setInventorySlotContents(index, null);
			}
			
			Iterator<Integer> ite = newItems.keySet().iterator();
			
			while(ite.hasNext()) {
				int key = ite.next();
				ItemStack item = newItems.get(key);
				chest.setInventorySlotContents(key, item.copy());
			}
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.chestsymmetrify.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}
