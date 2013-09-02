package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.filters.server.FilterServerFillInventory;
import mapmakingtools.filters.server.FilterServerPotionSpawner;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketFillInventory extends PacketMMT {

	public int x, y, z;
	
	public PacketFillInventory() {
		super(PacketTypeHandler.FILL_INVENTORY, false);
	}
	
	public PacketFillInventory(int x, int y, int z) {
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
		LogHelper.logDebug("Recive");
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				if(playerMP.openContainer != null && playerMP.openContainer instanceof ContainerFilter) {
					ContainerFilter container = (ContainerFilter)playerMP.openContainer;
					if(container.current != null && container.current instanceof FilterServerFillInventory) {
						FilterServerFillInventory armor = (FilterServerFillInventory)container.current;
						if (tile != null && tile instanceof IInventory) {
			    			for(int var8 = 0; var8 < ((IInventory)tile).getSizeInventory(); ++var8) {
			    				ItemStack newStack = null;
			    				if(container.getSlot(0).getStack() != null) {
			    					newStack = container.getSlot(0).getStack().copy();
			    					newStack.stackSize = -1;
			    				}
			    				((IInventory)tile).setInventorySlotContents(var8, newStack);
			    			}
			    		}
						//SpawnerHelper.setPotionType(player.worldObj.getBlockTileEntity(x, y, z), container.getSlot(0).getStack());
						player.sendChatToPlayer(ChatMessageComponent.func_111082_b("filter.fillInventory.complete", new Object[] {container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName()}));
					}
				}
			}
			
			/**
			if(QuickBuildHelper.isValidIds(text)) {
	    		int[] values = QuickBuildHelper.convertIdString(text);
	    		int blockId = values[0];
	    		int blockMeta = values[1];
	    		if (tile != null && tile instanceof IInventory) {
	    			for(int var8 = 0; var8 < ((IInventory)tile).getSizeInventory(); ++var8) {
	    				if(blockId > 0 && blockId <= Item.itemsList.length && Item.itemsList[blockId] != null) {
	    					((IInventory)tile).setInventorySlotContents(var8, new ItemStack(blockId, -1, blockMeta));
	    				}
	    			}
	    		}
	    		player.sendChatToPlayer(ChatMessageComponent.func_111082_b("filter.fillInventory.complete", new Object[] {new ItemStack(blockId, 1, blockMeta).getDisplayName()}));
			}
			**/
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

}
