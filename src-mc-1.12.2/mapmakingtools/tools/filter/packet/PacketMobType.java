package mapmakingtools.tools.filter.packet;

import java.io.IOException;
import java.util.List;

import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.api.enums.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketMobType extends AbstractServerMessage {

	public String mobId;
	public int minecartIndex;
	
	public PacketMobType() {}
	public PacketMobType(String mobId, int minecartIndex) {
		this.mobId = mobId;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.mobId = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.mobId);
		packetbuffer.writeInt(minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
			
			SpawnerUtil.setMobId(spawnerLogic, this.mobId, this.minecartIndex);
			
			if(container.getTargetType() == TargetType.BLOCK) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

				PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
				PacketUtil.sendTileEntityUpdateToWatching(spawner);
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobType.complete", this.mobId);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

}
