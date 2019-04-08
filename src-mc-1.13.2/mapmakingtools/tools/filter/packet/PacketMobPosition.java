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

public class PacketMobPosition {

	public String xPos, yPos, zPos;
	public boolean relative;
	public int minecartIndex;
	
	public PacketMobPosition(String xPos, String yPos, String zPos, boolean relative, int minecartIndex) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.relative = relative;
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketMobPosition msg, PacketBuffer buf) {
		buf.writeString(msg.xPos);
		buf.writeString(msg.yPos);
		buf.writeString(msg.zPos);
		buf.writeBoolean(msg.relative);
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketMobPosition decode(PacketBuffer buf) {
		String xPos = buf.readString(Integer.MAX_VALUE / 4);
		String yPos = buf.readString(Integer.MAX_VALUE / 4);
		String zPos = buf.readString(Integer.MAX_VALUE / 4);
		boolean relative = buf.readBoolean();
		int minecartIndex = buf.readInt();
		return new PacketMobPosition(xPos, yPos, zPos, relative, minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketMobPosition msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			
        			if(!Numbers.areDoubles(msg.xPos, msg.yPos, msg.zPos)) {
        				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobposition.notint");
        				chatComponent.getStyle().setItalic(true);
        				chatComponent.getStyle().setColor(TextFormatting.RED);
        				player.sendMessage(chatComponent);
        				return;
        			}
        			
        			double xPosNO = Numbers.getDouble(msg.xPos);
        			double yPosNO = Numbers.getDouble(msg.yPos);
        			double zPosNO = Numbers.getDouble(msg.zPos);
        			if(msg.relative) {
        				xPosNO += spawnerLogic.getSpawnerPosition().getX();
        				yPosNO += spawnerLogic.getSpawnerPosition().getY();
        				zPosNO += spawnerLogic.getSpawnerPosition().getZ();
        			}
        			
        			SpawnerUtil.setPosition(spawnerLogic, xPosNO, yPosNO, zPosNO, msg.minecartIndex);
        			
        			if(container.getTargetType() == TargetType.BLOCK) {
        				TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());

        				//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        				PacketUtil.sendTileEntityUpdateToWatching(spawner);
        			}
        		
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.mobposition.complete").applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
