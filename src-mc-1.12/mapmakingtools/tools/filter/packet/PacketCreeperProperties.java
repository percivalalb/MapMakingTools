package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCreeperProperties extends AbstractServerMessage {

	public BlockPos pos;
	public String fuseTime, explosionRadius;
	public int minecartIndex;
	
	public PacketCreeperProperties() {}
	public PacketCreeperProperties(BlockPos pos, String fuseTime, String explosionRadius, int minecartIndex) {
		this.pos = pos;
		this.fuseTime = fuseTime;
		this.explosionRadius = explosionRadius;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.fuseTime = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.explosionRadius = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeString(this.fuseTime);
		packetbuffer.writeString(this.explosionRadius);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			

			if(!NumberParse.areIntegers(this.fuseTime, this.explosionRadius)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.creeperproperties.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			int fuseTimeNO = NumberParse.getInteger(this.fuseTime);
			int explosionRadiusNO = NumberParse.getInteger(this.explosionRadius);
			
			SpawnerUtil.setCreeperFuse(spawner.getSpawnerBaseLogic(), fuseTimeNO, this.minecartIndex);
			SpawnerUtil.setCreeperExplosionRadius(spawner.getSpawnerBaseLogic(), explosionRadiusNO, this.minecartIndex);
			PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, pos, true), player);
			PacketUtil.sendTileEntityUpdateToWatching(spawner);
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.creeperproperties.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

}
