package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import mapmakingtools.helper.PacketHelper;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;

/**
 * @author ProPercivalalb
 */
public class PacketItemEditorUpdate extends IPacket {
	
	public int slotIndex;
	public ItemStack stack;
	
	public PacketItemEditorUpdate() {}
	public PacketItemEditorUpdate(ItemStack stack, int slotIndex) {
		this.stack = stack;
		this.slotIndex = slotIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.slotIndex = data.readInt();
		this.stack = PacketHelper.readItemStack(data);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(slotIndex);
		PacketHelper.writeItemStack(this.stack, dos);
	}
	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		player.inventory.setInventorySlotContents(this.slotIndex, this.stack);
	}
}
