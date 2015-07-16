package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmor extends IPacketPos {

	public int minecartIndex;
	
	public PacketMobArmor() {}
	public PacketMobArmor(BlockPos pos, int minecartIndex) {
		super(pos);
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof MobArmorServerFilter) {
				if(tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					MobArmorServerFilter filterCurrent = (MobArmorServerFilter)container.filterCurrent;
					ItemStack[] stack = filterCurrent.getInventory(container).contents; 
					SpawnerUtil.setMobArmor(spawner.func_145881_a(), stack[4], stack[3], stack[2], stack[1], stack[0], this.minecartIndex);
					SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
					
				    ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobArmor.complete");
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
				}
			}
		}
	}

}
