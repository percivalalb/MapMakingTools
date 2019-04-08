package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
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
public class PacketBabyMonster {

	public boolean baby;
	public int minecartIndex;
	
	public PacketBabyMonster(boolean baby, int minecartIndex) {
		this.baby = baby;
		this.minecartIndex = minecartIndex;
	}

	public static void encode(PacketBabyMonster msg, PacketBuffer buf) {
		buf.writeBoolean(msg.baby);
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketBabyMonster decode(PacketBuffer buf) {
		boolean baby = buf.readBoolean();
		int minecartIndex = buf.readInt();
		return new PacketBabyMonster(baby, minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketBabyMonster msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			if(SpawnerUtil.isSpawner(container)) {
        				MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			
        				SpawnerUtil.setBabyMonster(spawnerLogic, msg.baby, msg.minecartIndex);
        				
        				if(container.getTargetType() == TargetType.BLOCK) {
        					TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
        	
        					//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        					PacketUtil.sendTileEntityUpdateToWatching(spawner);
        				}
        			}
        			else if(container.getTargetType() == TargetType.ENTITY) {
        				Entity entity = container.getEntity();
        				if(entity instanceof EntityZombie) {
        					EntityZombie zombie = (EntityZombie)container.getEntity();
        					zombie.setChild(msg.baby);
        				}
        			}
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.babymonster.complete").applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
