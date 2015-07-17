package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateSpawner;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketMobType extends AbstractServerMessage {

	public BlockPos pos;
	public String mobId;
	public int minecartIndex;
	
	public PacketMobType() {}
	public PacketMobType(BlockPos pos, String mobId, int minecartIndex) {
		this.pos = pos;
		this.mobId = mobId;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		this.pos = BlockPos.fromLong(buffer.readLong());
		this.mobId = ByteBufUtils.readUTF8String(buffer);
		this.minecartIndex = buffer.readInt();
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeLong(this.pos.toLong());
		ByteBufUtils.writeUTF8String(buffer, this.mobId);
		buffer.writeInt(this.minecartIndex);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			SpawnerUtil.setMobId(spawner.func_145881_a(), this.mobId, this.minecartIndex);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobType.complete", this.mobId);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
			return new PacketUpdateSpawner(spawner, this.pos);
		}
		return null;
	}

}
