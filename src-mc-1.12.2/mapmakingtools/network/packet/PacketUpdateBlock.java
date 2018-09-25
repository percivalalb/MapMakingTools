package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateBlock extends AbstractClientMessage {

	public BlockPos pos;
	public NBTTagCompound tagCompound;
	public boolean onlyUpdate;
	
	public PacketUpdateBlock() {}
	public PacketUpdateBlock(TileEntity tileEntity, BlockPos pos, boolean onlyUpdate) {
		this.pos = pos;
		this.tagCompound = tileEntity.serializeNBT();
		this.onlyUpdate = onlyUpdate;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.tagCompound = packetbuffer.readCompoundTag();
		this.onlyUpdate = packetbuffer.readBoolean();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeCompoundTag(this.tagCompound);
		packetbuffer.writeBoolean(this.onlyUpdate);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tileEntity = player.world.getTileEntity(this.pos);
		
		if(tileEntity == null)
			return;

		FakeWorldManager.putTileEntity(tileEntity, player.world, this.pos, this.tagCompound);

		if(!this.onlyUpdate)
			PacketDispatcher.sendToServer(new PacketEditBlock(this.pos));
	}

}
