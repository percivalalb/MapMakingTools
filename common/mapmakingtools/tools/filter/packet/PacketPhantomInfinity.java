package mapmakingtools.tools.filter.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.network.packet.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class PacketPhantomInfinity extends IPacket {

	public int slotIndex;
	public boolean isUnlimited;
	
	public PacketPhantomInfinity() {}
	public PacketPhantomInfinity(int slotIndex, boolean isUnlimited) {
		this.slotIndex = slotIndex;
		this.isUnlimited = isUnlimited;
	}

	@Override
	public void read(ChannelHandlerContext ctx, ByteBuf bytes)  {
		this.slotIndex = bytes.readInt();
		this.isUnlimited = bytes.readBoolean();
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		bytes.writeInt(slotIndex);
		bytes.writeBoolean(isUnlimited);
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
