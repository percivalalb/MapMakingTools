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

public class PacketMobVelocity extends IPacketPos {

	public String xMotion, yMotion, zMotion;
	public int minecartIndex;
	
	public PacketMobVelocity() {}
	public PacketMobVelocity(BlockPos pos, String xMotion, String yMotion, String zMotion, int minecartIndex) {
		super(pos);
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.xMotion = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.yMotion = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.zMotion = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeStringToBuffer(this.xMotion);
		packetbuffer.writeStringToBuffer(this.yMotion);
		packetbuffer.writeStringToBuffer(this.zMotion);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areDoubles(this.xMotion, this.yMotion, this.zMotion)) {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobvelocity.notint");
				chatComponent.getChatStyle().setItalic(true);
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				player.addChatMessage(chatComponent);
				return;
			}
			
			double xMotionNO = NumberParse.getDouble(this.xMotion);
			double yMotionNO = NumberParse.getDouble(this.yMotion);
			double zMotionNO = NumberParse.getDouble(this.zMotion);
			
			SpawnerUtil.setVelocity(spawner.func_145881_a(), xMotionNO, yMotionNO, zMotionNO, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobvelocity.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}
}
