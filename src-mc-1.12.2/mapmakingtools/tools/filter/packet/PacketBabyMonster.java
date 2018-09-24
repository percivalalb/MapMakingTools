package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.api.enums.TargetType;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
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
public class PacketBabyMonster extends AbstractServerMessage {

	public boolean baby;
	public int minecartIndex;
	
	public PacketBabyMonster() {}
	public PacketBabyMonster(boolean baby, int minecartIndex) {
		this.baby = baby;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.baby = packetbuffer.readBoolean();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBoolean(this.baby);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(container.getTargetType() == TargetType.BLOCK) {
		
				TileEntity tile = player.world.getTileEntity(container.getBlockPos());
				if(tile instanceof TileEntityMobSpawner) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					
					SpawnerUtil.setBabyMonster(spawner.getSpawnerBaseLogic(), this.baby, this.minecartIndex);
					PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
					PacketUtil.sendTileEntityUpdateToWatching(spawner);
					
					TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.babymonster.complete");
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
			else if(container.getTargetType() == TargetType.ENTITY) {
				EntityZombie living = (EntityZombie)container.getEntity();
				living.setChild(this.baby);
			}
		}
	}

}
