package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.network.packet.MMTPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class PacketPhantomInfinity extends MMTPacket {

	public int slotIndex;
	public boolean isUnlimited;
	
	public PacketPhantomInfinity() {}
	public PacketPhantomInfinity(int slotIndex, boolean isUnlimited) {
		this.slotIndex = slotIndex;
		this.isUnlimited = isUnlimited;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		this.slotIndex = dis.readInt();
		this.isUnlimited = dis.readBoolean();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(slotIndex);
		dos.writeBoolean(isUnlimited);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer == null) 
			return;
		Container container = player.openContainer;
		
		Slot slot = (Slot)container.inventorySlots.get(this.slotIndex);
		if(slot instanceof IPhantomSlot)
			((IPhantomSlot)slot).setIsUnlimited(isUnlimited);
		if(slot.inventory instanceof IUnlimitedInventory)
			((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), isUnlimited);


	}

}
