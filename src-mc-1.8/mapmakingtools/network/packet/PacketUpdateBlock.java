package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.IPacketPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateBlock extends IPacketPos {

	public NBTTagCompound tagCompound;
	
	public PacketUpdateBlock() {}
	public PacketUpdateBlock(TileEntity tileEntity, BlockPos pos) {
		super(pos);
		this.tagCompound = new NBTTagCompound();
		tileEntity.writeToNBT(this.tagCompound);
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.tagCompound = packetbuffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeNBTTagCompoundToBuffer(this.tagCompound);
	}

	@Override
	public void execute(EntityPlayer player) {
		World world = ClientHelper.mc.thePlayer.worldObj;
		TileEntity tileEntity = world.getTileEntity(this.pos);
		
		if(tileEntity == null)
			return;
		
		FakeWorldManager.putTileEntity(tileEntity, world, this.pos, this.tagCompound);
		
		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketEditBlock(this.pos));
	}

}
