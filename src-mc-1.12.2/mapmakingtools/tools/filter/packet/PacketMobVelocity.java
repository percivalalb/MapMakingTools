package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.helper.Numbers;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

public class PacketMobVelocity extends AbstractServerMessage {

	public String xMotion, yMotion, zMotion;
	public int minecartIndex;
	
	public PacketMobVelocity() {}
	public PacketMobVelocity(String xMotion, String yMotion, String zMotion, int minecartIndex) {
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.xMotion = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.yMotion = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.zMotion = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.xMotion);
		packetbuffer.writeString(this.yMotion);
		packetbuffer.writeString(this.zMotion);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
			
			if(!Numbers.areDoubles(this.xMotion, this.yMotion, this.zMotion)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobvelocity.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			double xMotionNO = Numbers.getDouble(this.xMotion);
			double yMotionNO = Numbers.getDouble(this.yMotion);
			double zMotionNO = Numbers.getDouble(this.zMotion);
			
			SpawnerUtil.setVelocity(spawnerLogic, xMotionNO, yMotionNO, zMotionNO, this.minecartIndex);
			
			if(container.getTargetType() == TargetType.BLOCK) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

				PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
				PacketUtil.sendTileEntityUpdateToWatching(spawner);
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobvelocity.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
