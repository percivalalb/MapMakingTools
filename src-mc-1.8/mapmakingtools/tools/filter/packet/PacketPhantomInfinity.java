package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketPhantomInfinity extends AbstractServerMessage {

	public int slotIndex;
	public boolean isUnlimited;
	
	public PacketPhantomInfinity() {}
	public PacketPhantomInfinity(int slotIndex, boolean isUnlimited) {
		this.slotIndex = slotIndex;
		this.isUnlimited = isUnlimited;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.slotIndex = packetbuffer.readInt();
		this.isUnlimited = packetbuffer.readBoolean();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.slotIndex);
		packetbuffer.writeBoolean(this.isUnlimited);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(player.openContainer == null) 
			return;
		Container container = player.openContainer;
		
		Slot slot = (Slot)container.inventorySlots.get(this.slotIndex);
		if(slot instanceof IPhantomSlot)
			((IPhantomSlot)slot).setIsUnlimited(this.isUnlimited);
		if(slot.inventory instanceof IUnlimitedInventory)
			((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), this.isUnlimited);


	}

}
