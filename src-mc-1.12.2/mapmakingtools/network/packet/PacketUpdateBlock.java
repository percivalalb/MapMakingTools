package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.network.PacketDispatcher;
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
	public boolean onUpdate;
	
	public PacketUpdateBlock() {}
	public PacketUpdateBlock(TileEntity tileEntity, BlockPos pos, boolean onlyUpdate) {
		this.pos = pos;
		this.tagCompound = new NBTTagCompound();
		tileEntity.writeToNBT(this.tagCompound);
		FMLLog.info("On leave " + this.tagCompound.toString());
		this.onUpdate = onlyUpdate;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.tagCompound = packetbuffer.readCompoundTag();
		this.onUpdate = packetbuffer.readBoolean();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeCompoundTag(this.tagCompound);
		packetbuffer.writeBoolean(this.onUpdate);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		World world = player.world;
		TileEntity tileEntity = world.getTileEntity(this.pos);
		
		if(tileEntity == null)
			return;
		FMLLog.info(tileEntity.getClass().getName());
		//tileEntity.readFromNBT(this.tagCompound);

		FakeWorldManager.putTileEntity(tileEntity, world, this.pos, this.tagCompound);
		if(FakeWorldManager.getTileEntity(world, this.pos) instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)FakeWorldManager.getTileEntity(world, this.pos);
			FMLLog.info(" Size: " + SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic()).size());
		}
		
		FMLLog.info("On arrive " + this.tagCompound.toString());
		if(!this.onUpdate)
			PacketDispatcher.sendToServer(new PacketEditBlock(this.pos));
	}

}
