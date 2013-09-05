package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketConvertToDropper extends PacketMMT {

	public int x, y, z;
	
	public PacketConvertToDropper() {
		super(PacketTypeHandler.CONVERT_TO_DROPPER, false);
	}
	
	public PacketConvertToDropper(int x, int y, int z) {
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
			int blockId = player.worldObj.getBlockId(x, y, z);
			int blockMeta = player.worldObj.getBlockMetadata(x, y, z);
			LogHelper.logDebug("Dispenser meta: " + blockMeta);
			if(blockId == Block.dispenser.blockID) {
				TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
				if(tile instanceof TileEntityDispenser) {
					TileEntityDispenser dispenser = (TileEntityDispenser)tile;
					ItemStack[] slots = new ItemStack[dispenser.getSizeInventory()];
					for(int slotCount = 0; slotCount < dispenser.getSizeInventory(); ++slotCount) {
						ItemStack stack = dispenser.getStackInSlot(slotCount);
						if(stack != null) {
							slots[slotCount] = stack.copy();
							dispenser.setInventorySlotContents(slotCount, (ItemStack)null);
						}
					}
					player.worldObj.setBlock(x, y, z, Block.dropper.blockID);
					player.worldObj.setBlockMetadataWithNotify(x, y, z, blockMeta, 2);
					tile = player.worldObj.getBlockTileEntity(x, y, z);
					if(tile instanceof TileEntityDropper) {
						TileEntityDropper dropper = (TileEntityDropper)tile;
						for(int slotCount = 0; slotCount < slots.length; ++slotCount) {
							dropper.setInventorySlotContents(slotCount, slots[slotCount]);
						}
					}
				}
				player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("filter.convertToDropper.complete"));
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}

}
