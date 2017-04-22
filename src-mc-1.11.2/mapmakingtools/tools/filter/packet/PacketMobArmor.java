package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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
public class PacketMobArmor extends AbstractServerMessage {

	public BlockPos pos;
	public int minecartIndex;
	
	public PacketMobArmor() {}
	public PacketMobArmor(BlockPos pos, int minecartIndex) {
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
					IInventory inventory = filterCurrent.getInventory(container); 
					SpawnerUtil.setMobArmor(spawner.getSpawnerBaseLogic(), inventory.getStackInSlot(4), inventory.getStackInSlot(3), inventory.getStackInSlot(2), inventory.getStackInSlot(1), inventory.getStackInSlot(0), this.minecartIndex);
					PacketUtil.sendTileEntityUpdateToWatching(spawner);
					
				    TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.complete");
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
		}
	}

}
