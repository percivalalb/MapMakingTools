package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
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
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketFillInventory extends PacketMMT {

	public int x, y, z;
	public String text;
	
	public PacketFillInventory() {
		super(PacketTypeHandler.FILL_INVENTORY, false);
	}
	
	public PacketFillInventory(int x, int y, int z, String text) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.text = text;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.text = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeUTF(text);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
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
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}

}
