package mapmakingtools.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
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
import mapmakingtools.network.packet.PacketUpdateSpawner;
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
import mapmakingtools.tools.filter.packet.PacketMobArmorAddIndex;
import mapmakingtools.tools.filter.packet.PacketMobArmorRemoveIndex;
import mapmakingtools.tools.filter.packet.PacketMobArmorUpdate;
import mapmakingtools.tools.filter.packet.PacketMobPosition;
import mapmakingtools.tools.filter.packet.PacketMobType;
import mapmakingtools.tools.filter.packet.PacketMobVelocity;
import mapmakingtools.tools.filter.packet.PacketPhantomInfinity;
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
		registerMessage(PacketMobArmorAddIndex.class);
		registerMessage(PacketMobArmorRemoveIndex.class);
		registerMessage(PacketMobArmorUpdate.class);
		registerMessage(PacketMobPosition.class);
		registerMessage(PacketMobType.class);
		registerMessage(PacketMobVelocity.class);
		registerMessage(PacketPhantomInfinity.class);
		registerMessage(PacketSpawnerTimings.class);
		registerMessage(PacketVillagerProfession.class);
		registerMessage(PacketVillagerRecipeAmounts.class);
		registerMessage(PacketVillagerShop.class);
		registerMessage(PacketUpdateSpawner.class);
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

	public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PacketDispatcher.sendToAllAround(message, player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, range);
	}
	
	public static final void sendToAllAround(IMessage message, TileEntity tileEntity, double range) {
		PacketDispatcher.sendToAllAround(message, tileEntity.getWorldObj().provider.dimensionId, tileEntity.xCoord + 0.5D, tileEntity.yCoord + 0.5D, tileEntity.zCoord + 0.5D, range);
	}

	public static final void sendToDimension(IMessage message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}

	public static final void sendToServer(IMessage message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
	
	public static final Packet getPacket(IMessage message) {
		return PacketDispatcher.dispatcher.getPacketFrom(message);
	}
}
