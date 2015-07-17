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

public class PacketMobPosition extends AbstractServerMessage {

	public BlockPos pos;
	public String xPos, yPos, zPos;
	public boolean relative;
	public int minecartIndex;
	
	public PacketMobPosition() {}
	public PacketMobPosition(BlockPos pos, String xPos, String yPos, String zPos, boolean relative, int minecartIndex) {
		this.pos = pos;
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.relative = relative;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.xPos = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.yPos = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.zPos = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.relative = packetbuffer.readBoolean();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeStringToBuffer(this.xPos);
		packetbuffer.writeStringToBuffer(this.yPos);
		packetbuffer.writeStringToBuffer(this.zPos);
		packetbuffer.writeBoolean(this.relative);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areDoubles(this.xPos, this.yPos, this.zPos)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobposition.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return null;
			}
			
			double xPosNO = NumberParse.getDouble(this.xPos);
			double yPosNO = NumberParse.getDouble(this.yPos);
			double zPosNO = NumberParse.getDouble(this.zPos);
			if(this.relative) {
				xPosNO += this.pos.getX();
				yPosNO += this.pos.getY();
				zPosNO += this.pos.getZ();
			}
			
			SpawnerUtil.setPosition(spawner.func_145881_a(), xPosNO, yPosNO, zPosNO, this.minecartIndex);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobposition.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			return new PacketUpdateSpawner(spawner, this.pos);
		}
		
		return null;
	}
}
