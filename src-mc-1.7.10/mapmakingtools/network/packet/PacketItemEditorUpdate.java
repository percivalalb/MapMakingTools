package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

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
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.slotIndex = packetbuffer.readInt();
		this.stack = packetbuffer.readItemStackFromBuffer();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.slotIndex);
		packetbuffer.writeItemStackToBuffer(this.stack);
	}
	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		System.out.println(player);
		
		player.inventory.setInventorySlotContents(this.slotIndex, this.stack);
		player.inventory.markDirty();
	}
}
