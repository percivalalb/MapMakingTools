package mapmakingtools.tools.filter.packet;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.helper.Numbers;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketCreeperProperties {

	public String fuseTime, explosionRadius;
	public int minecartIndex;
	
	public PacketCreeperProperties(String fuseTime, String explosionRadius, int minecartIndex) {
		this.fuseTime = fuseTime;
		this.explosionRadius = explosionRadius;
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketCreeperProperties msg, PacketBuffer buf) {
		buf.writeString(msg.fuseTime);
		buf.writeString(msg.explosionRadius);
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketCreeperProperties decode(PacketBuffer buf) {
		String fuseTime = buf.readString(Integer.MAX_VALUE / 4);
		String explosionRadius = buf.readString(Integer.MAX_VALUE / 4);
		int minecartIndex = buf.readInt();
		return new PacketCreeperProperties(fuseTime, explosionRadius, minecartIndex);
	}
	
	private static Field FIELD_FUSE_TIME = ReflectionHelper.getField(EntityCreeper.class, 5);
	private static Field FIELD_EXPOSION_RADIUS = ReflectionHelper.getField(EntityCreeper.class, 6);
	
	public static class Handler {
        public static void handle(final PacketCreeperProperties msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			if(!Numbers.areIntegers(msg.fuseTime, msg.explosionRadius)) {
        				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.creeperproperties.notint");
        				chatComponent.getStyle().setItalic(true);
        				chatComponent.getStyle().setColor(TextFormatting.RED);
        				player.sendMessage(chatComponent);
        				return;
        			}
        			
        			int fuseTimeNO = Numbers.parse(msg.fuseTime);
        			int explosionRadiusNO = Numbers.parse(msg.explosionRadius);
        			
        			if(SpawnerUtil.isSpawner(container)) {
        				MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);

        				SpawnerUtil.setCreeperFuse(spawnerLogic, fuseTimeNO, msg.minecartIndex);
        				SpawnerUtil.setCreeperExplosionRadius(spawnerLogic, explosionRadiusNO, msg.minecartIndex);
        				
        				if(container.getTargetType() == TargetType.BLOCK) {
        					TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
        	
        					//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
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
            });

            ctx.get().setPacketHandled(true);
        }
	}

}
