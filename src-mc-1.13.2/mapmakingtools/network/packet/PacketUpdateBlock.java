package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateBlock {

	public BlockPos pos;
	public NBTTagCompound tagCompound;
	public boolean onlyUpdate;
	
	public PacketUpdateBlock(TileEntity tileEntity, BlockPos pos, boolean onlyUpdate) {
		this(pos, tileEntity.serializeNBT(), onlyUpdate);
	}
	
	public PacketUpdateBlock(BlockPos pos, NBTTagCompound tagCompound, boolean onlyUpdate) {
		this.pos = pos;
		this.tagCompound = tagCompound;
		this.onlyUpdate = onlyUpdate;
	}
	
	public static void encode(PacketUpdateBlock msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
		buf.writeCompoundTag(msg.tagCompound);
		buf.writeBoolean(msg.onlyUpdate);
	}
	
	public static PacketUpdateBlock decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		NBTTagCompound tagCompound = buf.readCompoundTag();
		boolean onlyUpdate = buf.readBoolean();
		return new PacketUpdateBlock(pos, tagCompound, onlyUpdate);
	}
	
	public static class Handler {
        public static void handle(final PacketUpdateBlock msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		TileEntity tileEntity = player.world.getTileEntity(msg.pos);
        		
        		if(tileEntity == null)
        			return;

        		FakeWorldManager.putTileEntity(tileEntity, player.world, msg.pos, msg.tagCompound);

        	//	if(!msg.onlyUpdate)
        			//TODO PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketEditBlock(msg.pos));
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
