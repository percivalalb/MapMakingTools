package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.ItemSpawnerServerFilter;
import mapmakingtools.util.PacketUtil;
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
			if(container.filterCurrent instanceof ItemSpawnerServerFilter) {
				
				ItemSpawnerServerFilter filterCurrent = (ItemSpawnerServerFilter)container.filterCurrent;
				if (tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					
					ItemStack item = container.getSlot(0).getStack().copy();
					SpawnerUtil.setItemType(spawner.getSpawnerBaseLogic(), item, this.minecartIndex);
					PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, pos, true), player);
					PacketUtil.sendTileEntityUpdateToWatching(spawner);
					
			    	TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.changeitem.complete", container.getSlot(0).getStack() == null ? "Nothing" :container.getSlot(0).getStack().getDisplayName());
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
		}
	}

}
