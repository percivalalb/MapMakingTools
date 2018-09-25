package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.api.enums.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
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
			
			if(SpawnerUtil.isSpawner(container)) {
				MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
			
				SpawnerUtil.setBabyMonster(spawnerLogic, this.baby, this.minecartIndex);
				
				if(container.getTargetType() == TargetType.BLOCK) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
	
					PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
					PacketUtil.sendTileEntityUpdateToWatching(spawner);
				}
			}
			else if(container.getTargetType() == TargetType.ENTITY) {
				Entity entity = container.getEntity();
				if(entity instanceof EntityZombie) {
					EntityZombie zombie = (EntityZombie)container.getEntity();
					zombie.setChild(this.baby);
				}
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.babymonster.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
