package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.NumberParse;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

public class PacketMobVelocity extends AbstractServerMessage {

	public BlockPos pos;
	public String xMotion, yMotion, zMotion;
	public int minecartIndex;
	
	public PacketMobVelocity() {}
	public PacketMobVelocity(BlockPos pos, String xMotion, String yMotion, String zMotion, int minecartIndex) {
		this.pos = pos;
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.xMotion = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.yMotion = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.zMotion = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeString(this.xMotion);
		packetbuffer.writeString(this.yMotion);
		packetbuffer.writeString(this.zMotion);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.world.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(!NumberParse.areDoubles(this.xMotion, this.yMotion, this.zMotion)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobvelocity.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			double xMotionNO = NumberParse.getDouble(this.xMotion);
			double yMotionNO = NumberParse.getDouble(this.yMotion);
			double zMotionNO = NumberParse.getDouble(this.zMotion);
			
			SpawnerUtil.setVelocity(spawner.getSpawnerBaseLogic(), xMotionNO, yMotionNO, zMotionNO, this.minecartIndex);
			PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, pos, true), player);
			PacketUtil.sendTileEntityUpdateToWatching(spawner);
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobvelocity.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
