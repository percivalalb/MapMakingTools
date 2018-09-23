package mapmakingtools.network;

import mapmakingtools.lib.Reference;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.network.packet.PacketEditBlock;
import mapmakingtools.network.packet.PacketEditEntity;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import mapmakingtools.network.packet.PacketSelectedFilter;
import mapmakingtools.network.packet.PacketSetPoint1;
import mapmakingtools.network.packet.PacketSetPoint2;
import mapmakingtools.network.packet.PacketSkullModify;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.network.packet.PacketUpdateEntity;
import mapmakingtools.tools.filter.packet.PacketBabyMonster;
import mapmakingtools.tools.filter.packet.PacketChestSymmetrify;
import mapmakingtools.tools.filter.packet.PacketCommandBlockAlias;
import mapmakingtools.tools.filter.packet.PacketConvertToDispenser;
import mapmakingtools.tools.filter.packet.PacketConvertToDropper;
import mapmakingtools.tools.filter.packet.PacketCreeperProperties;
import mapmakingtools.tools.filter.packet.PacketCustomGive;
import mapmakingtools.tools.filter.packet.PacketFillInventory;
import mapmakingtools.tools.filter.packet.PacketItemSpawner;
import mapmakingtools.tools.filter.packet.PacketMobArmor;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsAdd;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsRemove;
import mapmakingtools.tools.filter.packet.PacketMobArmorUpdate;
import mapmakingtools.tools.filter.packet.PacketMobPosition;
import mapmakingtools.tools.filter.packet.PacketMobType;
import mapmakingtools.tools.filter.packet.PacketMobVelocity;
import mapmakingtools.tools.filter.packet.PacketSignEdit;
import mapmakingtools.tools.filter.packet.PacketSpawnerTimings;
import mapmakingtools.tools.filter.packet.PacketVillagerProfession;
import mapmakingtools.tools.filter.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.tools.filter.packet.PacketVillagerShop;
import mapmakingtools.tools.worldtransfer.PacketAddArea;
import mapmakingtools.tools.worldtransfer.PacketPaste;
import mapmakingtools.tools.worldtransfer.PacketPasteNotify;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 * Thanks to coolAlias for providing the tutorial 
 * that contains most of this network handler code
 * https://github.com/coolAlias/Tutorial-Demo
 */
public class PacketDispatcher {
	
	private static int packetId = 0;

	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL_NAME);

	public static final void registerPackets() {
		registerMessage(PacketBiomeUpdate.class);
		registerMessage(PacketEditBlock.class);
		registerMessage(PacketEditEntity.class);
		registerMessage(PacketItemEditorUpdate.class);
		registerMessage(PacketOpenItemEditor.class);
		registerMessage(PacketSelectedFilter.class);
		registerMessage(PacketSetPoint1.class);
		registerMessage(PacketSetPoint2.class);
		registerMessage(PacketSkullModify.class);
		registerMessage(PacketUpdateBlock.class);
		registerMessage(PacketUpdateEntity.class);
		registerMessage(PacketAddArea.class);
		registerMessage(PacketPaste.class);
		registerMessage(PacketPasteNotify.class);
		registerMessage(PacketBabyMonster.class);
		registerMessage(PacketChestSymmetrify.class);
		registerMessage(PacketCommandBlockAlias.class);
		registerMessage(PacketConvertToDispenser.class);
		registerMessage(PacketConvertToDropper.class);
		registerMessage(PacketCreeperProperties.class);
		registerMessage(PacketCustomGive.class);
		registerMessage(PacketFillInventory.class);
		registerMessage(PacketItemSpawner.class);
		registerMessage(PacketMobArmor.class);
		registerMessage(PacketPotentialSpawnsAdd.class);
		registerMessage(PacketPotentialSpawnsRemove.class);
		registerMessage(PacketMobArmorUpdate.class);
		registerMessage(PacketMobPosition.class);
		registerMessage(PacketMobType.class);
		registerMessage(PacketMobVelocity.class);
		registerMessage(PacketSpawnerTimings.class);
		registerMessage(PacketVillagerProfession.class);
		registerMessage(PacketVillagerRecipeAmounts.class);
		registerMessage(PacketVillagerShop.class);
		registerMessage(PacketSignEdit.class);
	}
	
	private static final <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
		if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
		} 
		else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		} 
		else {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		}
	}

	public static final void sendTo(IMessage message, EntityPlayer player) {
		if(player instanceof EntityPlayerMP)
			PacketDispatcher.dispatcher.sendTo(message, (EntityPlayerMP)player);
	}
	
	public static void sendToAll(IMessage message) {
		PacketDispatcher.dispatcher.sendToAll(message);
	}

	
	
	public static final void sendToAllAround(IMessage message, TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new TargetPoint(dimension, x, y, z, range));
	}

	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PacketDispatcher.sendToAllAround(message, player.world.provider.getDimension(), player.posX, player.posY, player.posZ, range);
	}
	
	public static final void sendToAllAround(IMessage message, TileEntity tileEntity, double range) {
		PacketDispatcher.sendToAllAround(message, tileEntity.getWorld().provider.getDimension(), tileEntity.getPos().getX() + 0.5D, tileEntity.getPos().getY() + 0.5D, tileEntity.getPos().getZ() + 0.5D, range);
	}
	
	public static void sendToAllTrackingChunk(IMessage message, World world, int chunkX, int chunkZ) {
		//TODO
		/**if(world instanceof WorldServer) {
			for(Object obj : ServerHelper.mcServer.getConfigurationManager().playerEntityList) {
				EntityPlayerMP player = (EntityPlayerMP)obj;
				if(((WorldServer)world).getPlayerManager().isPlayerWatchingChunk(player, chunkX, chunkZ));
					sendTo(message, player);
			}
		}**/
	}
	
	public static void sendToAllTrackingBlock(IMessage message, World world, BlockPos pos) {
		sendToAllTrackingChunk(message, world, pos.getX() >> 4, pos.getZ() >> 4);
	}
	
	public static void sendToAllTrackingTileEntity(IMessage message, TileEntity tileEntity) {
		sendToAllTrackingBlock(message, tileEntity.getWorld(), tileEntity.getPos());
	}

	public static final void sendToDimension(IMessage message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}
	
	/**
	public static void sendToAllTracking(Packet packet, Entity entity) {
		((WorldServer)entity.world).getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity, packet);
	}

	public static void sendToAllAssociated(Packet packet, Entity entity) {
		((WorldServer)entity.world).getEntityTracker().sendPacketToAllAssociatedPlayers(entity, packet);
	}
	 **/
	public static final void sendToServer(IMessage message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
	
	
	
	public static final Packet getPacket(IMessage message) {
		return PacketDispatcher.dispatcher.getPacketFrom(message);
	}
}
