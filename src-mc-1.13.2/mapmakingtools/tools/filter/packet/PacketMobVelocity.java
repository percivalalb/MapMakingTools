package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.helper.Numbers;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketMobVelocity {

	public String xMotion, yMotion, zMotion;
	public int minecartIndex;
	
	public PacketMobVelocity(String xMotion, String yMotion, String zMotion, int minecartIndex) {
		this.xMotion = xMotion;
		this.yMotion = yMotion;
		this.zMotion = zMotion;
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketMobVelocity msg, PacketBuffer buf) {
		buf.writeString(msg.xMotion);
		buf.writeString(msg.yMotion);
		buf.writeString(msg.zMotion);
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketMobVelocity decode(PacketBuffer buf) {
		String xMotion = buf.readString(Integer.MAX_VALUE / 4);
		String yMotion = buf.readString(Integer.MAX_VALUE / 4);
		String zMotion = buf.readString(Integer.MAX_VALUE / 4);
		int minecartIndex = buf.readInt();
		return new PacketMobVelocity(xMotion, yMotion, zMotion, minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketMobVelocity msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			
        			if(!Numbers.areDoubles(msg.xMotion, msg.yMotion, msg.zMotion)) {
        				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobvelocity.notint");
        				chatComponent.getStyle().setItalic(true);
        				chatComponent.getStyle().setColor(TextFormatting.RED);
        				player.sendMessage(chatComponent);
        				return;
        			}
        			
        			double xMotionNO = Numbers.getDouble(msg.xMotion);
        			double yMotionNO = Numbers.getDouble(msg.yMotion);
        			double zMotionNO = Numbers.getDouble(msg.zMotion);
        			
        			SpawnerUtil.setVelocity(spawnerLogic, xMotionNO, yMotionNO, zMotionNO, msg.minecartIndex);
        			
        			if(container.getTargetType() == TargetType.BLOCK) {
        				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

        				//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        				PacketUtil.sendTileEntityUpdateToWatching(spawner);
        			}
        			
        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobvelocity.complete");
        			chatComponent.getStyle().setItalic(true);
        			player.sendMessage(chatComponent);
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
