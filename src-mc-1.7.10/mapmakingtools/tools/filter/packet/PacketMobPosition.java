package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.NumberParse;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class PacketMobPosition extends IPacketPos {

	public String xPos, yPos, zPos;
	public boolean relative;
	public int minecartIndex;
	
	public PacketMobPosition() {}
	public PacketMobPosition(BlockPos pos, String xPos, String yPos, String zPos, boolean relative, int minecartIndex) {
		super(pos);
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.relative = relative;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.xPos = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.yPos = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.zPos = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.relative = packetbuffer.readBoolean();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeStringToBuffer(this.xPos);
		packetbuffer.writeStringToBuffer(this.yPos);
		packetbuffer.writeStringToBuffer(this.zPos);
		packetbuffer.writeBoolean(this.relative);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areDoubles(this.xPos, this.yPos, this.zPos)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobposition.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
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
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobposition.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}
}
