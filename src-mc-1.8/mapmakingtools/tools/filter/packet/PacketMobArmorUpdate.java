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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmorUpdate extends IPacketPos {

	public int minecartIndex;
	
	public PacketMobArmorUpdate() {}
	public PacketMobArmorUpdate(BlockPos pos, int minecartIndex) {
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
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof MobArmorServerFilter) {
				if(tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					MobArmorServerFilter filterCurrent = (MobArmorServerFilter)container.filterCurrent;
					ItemStack[] mobArmor = SpawnerUtil.getMobArmor(spawner.getSpawnerBaseLogic(), this.minecartIndex);
					for(int i = 0; i < mobArmor.length; ++i) {
						filterCurrent.getInventory(container).contents[i] = mobArmor[i];
				    }
					
				    ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobArmor.update");
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
				}
			}
		}
	}

}
