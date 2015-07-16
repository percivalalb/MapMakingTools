package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketMobType extends IPacketPos {

	public String mobId;
	public int minecartIndex;
	
	public PacketMobType() {}
	public PacketMobType(BlockPos pos, String mobId, int minecartIndex) {
		super(pos);
		this.mobId = mobId;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.mobId = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeString(this.mobId);
		packetbuffer.writeInt(minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			SpawnerUtil.setMobId(spawner.getSpawnerBaseLogic(), this.mobId, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobType.complete", this.mobId);
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}
