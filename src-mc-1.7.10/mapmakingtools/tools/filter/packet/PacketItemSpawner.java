package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateSpawner;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.ItemSpawnerServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketItemSpawner extends AbstractServerMessage {

	public BlockPos pos;
	public int minecartIndex;
	
	public PacketItemSpawner() {}
	public PacketItemSpawner(BlockPos pos, int minecartIndex) {
		this.pos = pos;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof ItemSpawnerServerFilter) {
				
				ItemSpawnerServerFilter filterCurrent = (ItemSpawnerServerFilter)container.filterCurrent;
				if (tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					
					ItemStack item = container.getSlot(0).getStack().copy();
					SpawnerUtil.setItemType(spawner.func_145881_a(), item, this.minecartIndex);
					
			    	ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.changeitem.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
					return new PacketUpdateSpawner(spawner, pos);
				}
			}
		}
		
		return null;
	}

}
