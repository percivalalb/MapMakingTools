package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketItemEditorUpdate extends AbstractServerMessage {
	
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
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		System.out.println(player);
		
		player.inventory.setInventorySlotContents(this.slotIndex, this.stack);
		player.inventory.markDirty();
	}
}
