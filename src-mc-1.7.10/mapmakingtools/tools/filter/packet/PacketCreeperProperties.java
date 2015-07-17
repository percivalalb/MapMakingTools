package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.helper.NumberParse;
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
import net.minecraft.util.EnumChatFormatting;

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
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.fuseTime = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.explosionRadius = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeStringToBuffer(this.fuseTime);
		packetbuffer.writeStringToBuffer(this.explosionRadius);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			

			if(!NumberParse.areIntegers(this.fuseTime, this.explosionRadius)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.creeperproperties.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return null;
			}
			
			int fuseTimeNO = NumberParse.getInteger(this.fuseTime);
			int explosionRadiusNO = NumberParse.getInteger(this.explosionRadius);
			
			SpawnerUtil.setCreeperFuse(spawner.func_145881_a(), fuseTimeNO, this.minecartIndex);
			SpawnerUtil.setCreeperExplosionRadius(spawner.func_145881_a(), explosionRadiusNO, this.minecartIndex);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.creeperproperties.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
			return new PacketUpdateSpawner(spawner, pos);
		}
		return null;
	}

}
