package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.network.PacketDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

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
		this.pos = packetbuffer.readBlockPos();
		this.tagCompound = packetbuffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeNBTTagCompoundToBuffer(this.tagCompound);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		World world = ClientHelper.mc.thePlayer.worldObj;
		TileEntity tileEntity = world.getTileEntity(this.pos);
		
		if(tileEntity == null)
			return;
		
		FakeWorldManager.putTileEntity(tileEntity, world, this.pos, this.tagCompound);
		
		PacketDispatcher.sendToServer(new PacketEditBlock(this.pos));
	}

}
