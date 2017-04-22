package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

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
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.mobId = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeString(this.mobId);
		packetbuffer.writeInt(minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			SpawnerUtil.setMobId(spawner.getSpawnerBaseLogic(), this.mobId, this.minecartIndex);
			PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, pos, true), player);
			PacketUtil.sendTileEntityUpdateToWatching(spawner);
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobType.complete", this.mobId);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

}
