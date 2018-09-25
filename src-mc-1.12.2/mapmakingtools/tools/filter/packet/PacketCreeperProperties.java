package mapmakingtools.tools.filter.packet;

import java.io.IOException;
import java.lang.reflect.Field;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.helper.Numbers;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCreeperProperties extends AbstractServerMessage {

	public String fuseTime, explosionRadius;
	public int minecartIndex;
	
	public PacketCreeperProperties() {}
	public PacketCreeperProperties(String fuseTime, String explosionRadius, int minecartIndex) {
		this.fuseTime = fuseTime;
		this.explosionRadius = explosionRadius;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.fuseTime = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.explosionRadius = packetbuffer.readString(Integer.MAX_VALUE / 4);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.fuseTime);
		packetbuffer.writeString(this.explosionRadius);
		packetbuffer.writeInt(this.minecartIndex);
	}

	private static Field FIELD_FUSE_TIME = ReflectionHelper.getField(EntityCreeper.class, 5);
	private static Field FIELD_EXPOSION_RADIUS = ReflectionHelper.getField(EntityCreeper.class, 6);
	
	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			if(!Numbers.areIntegers(this.fuseTime, this.explosionRadius)) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.creeperproperties.notint");
				chatComponent.getStyle().setItalic(true);
				chatComponent.getStyle().setColor(TextFormatting.RED);
				player.sendMessage(chatComponent);
				return;
			}
			
			int fuseTimeNO = Numbers.parse(this.fuseTime);
			int explosionRadiusNO = Numbers.parse(this.explosionRadius);
			
			if(SpawnerUtil.isSpawner(container)) {
				MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);

				SpawnerUtil.setCreeperFuse(spawnerLogic, fuseTimeNO, this.minecartIndex);
				SpawnerUtil.setCreeperExplosionRadius(spawnerLogic, explosionRadiusNO, this.minecartIndex);
				
				if(container.getTargetType() == TargetType.BLOCK) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
	
					PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
					PacketUtil.sendTileEntityUpdateToWatching(spawner);
				}
			}
			else if(container.getTargetType() == TargetType.ENTITY) {
				EntityCreeper creeper = (EntityCreeper)container.getEntity();
				ReflectionHelper.setField(FIELD_FUSE_TIME, creeper, fuseTimeNO);
				ReflectionHelper.setField(FIELD_EXPOSION_RADIUS, creeper, explosionRadiusNO);
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.babymonster.complete");
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}

}
