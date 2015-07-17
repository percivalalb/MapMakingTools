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
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateBlock extends AbstractClientMessage {

	public BlockPos pos;
	public NBTTagCompound tagCompound;
	
	public PacketUpdateBlock() {}
	public PacketUpdateBlock(TileEntity tileEntity, BlockPos pos) {
		this.pos = pos;
		this.tagCompound = new NBTTagCompound();
		tileEntity.writeToNBT(this.tagCompound);
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
		World world = ClientHelper.mc.thePlayer.worldObj;
		TileEntity tileEntity = world.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		
		if(tileEntity == null)
			return null;
		
		FakeWorldManager.putTileEntity(tileEntity, world, this.pos, this.tagCompound);
		
		return new PacketEditBlock(this.pos);
	}

}
