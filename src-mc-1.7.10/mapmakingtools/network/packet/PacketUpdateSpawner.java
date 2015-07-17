package mapmakingtools.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateSpawner extends AbstractClientMessage {

	public BlockPos pos;
	public NBTTagCompound tagCompound;
	
	public PacketUpdateSpawner() {}
	public PacketUpdateSpawner(TileEntityMobSpawner tileEntity, BlockPos pos) {
		this.pos = pos;
		this.tagCompound = new NBTTagCompound();
		tileEntity.writeToNBT(this.tagCompound);
		this.tagCompound.removeTag("SpawnPotentials");
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.tagCompound = packetbuffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeNBTTagCompoundToBuffer(this.tagCompound);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		World world = player.worldObj;
		TileEntity tileEntity = world.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		
		if(tileEntity == null)
			return null;

		tileEntity.readFromNBT(this.tagCompound);
		world.setBlockMetadataWithNotify(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 1, 3);
		
		return null;
	}

}
