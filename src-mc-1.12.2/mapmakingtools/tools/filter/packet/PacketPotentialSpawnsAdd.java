package mapmakingtools.tools.filter.packet;

import java.io.IOException;
import java.util.List;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
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
public class PacketPotentialSpawnsAdd extends AbstractServerMessage {

	public int minecartIndex;
	
	public PacketPotentialSpawnsAdd() {}
	public PacketPotentialSpawnsAdd(int minecartIndex) {
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawnerLogic);
			
			WeightedSpawnerEntity randomMinecart = new WeightedSpawnerEntity();
			minecarts.add(this.minecartIndex, randomMinecart);
			spawnerLogic.setNextSpawnData(randomMinecart);
			
			if(container.getTargetType() == TargetType.BLOCK) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
				
				PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
				PacketUtil.sendTileEntityUpdateToWatching(spawner);
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.addIndex");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
