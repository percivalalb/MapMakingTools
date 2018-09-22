package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmorUpdate extends AbstractServerMessage {

	public BlockPos pos;
	public int minecartIndex;
	
	public PacketMobArmorUpdate() {}
	public PacketMobArmorUpdate(BlockPos pos, int minecartIndex) {
		this.pos = pos;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.filterCurrent instanceof MobArmorServerFilter) {
				if(tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					MobArmorServerFilter filterCurrent = (MobArmorServerFilter)container.filterCurrent;
					ItemStack[] mobArmor = SpawnerUtil.getMobArmor(spawner.getSpawnerBaseLogic(), this.minecartIndex);
					ItemStack[] mobHeld = SpawnerUtil.getMobHeldItems(spawner.getSpawnerBaseLogic(), this.minecartIndex);
					
					for(int i = 0; i < mobHeld.length; ++i) {
						filterCurrent.getInventory(container).setInventorySlotContents(1 - i, mobHeld[i]);
				    }
					for(int i = 0; i < mobArmor.length; ++i) {
						filterCurrent.getInventory(container).setInventorySlotContents(i + 2, mobArmor[i]);
				    }
					
				    TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.update");
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
		}
	}

}
