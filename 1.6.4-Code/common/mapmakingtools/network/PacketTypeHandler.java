package mapmakingtools.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import cpw.mods.fml.relauncher.Side;

import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.packet.PacketBabyMonster;
import mapmakingtools.network.packet.PacketBeaconModify;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.network.packet.PacketBuildLeftClick;
import mapmakingtools.network.packet.PacketBuildRightClick;
import mapmakingtools.network.packet.PacketChangeWatchPlayer;
import mapmakingtools.network.packet.PacketChestSymmetrify;
import mapmakingtools.network.packet.PacketCommandBlockAlias;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketCreeperProperties;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketFilterPageMenu;
import mapmakingtools.network.packet.PacketFly;
import mapmakingtools.network.packet.PacketFrameDropChance;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import mapmakingtools.network.packet.PacketItemSpawner;
import mapmakingtools.network.packet.PacketMMT;
import mapmakingtools.network.packet.PacketMaxVillagerRecipes;
import mapmakingtools.network.packet.PacketMobArmor;
import mapmakingtools.network.packet.PacketMobPosition;
import mapmakingtools.network.packet.PacketMobType;
import mapmakingtools.network.packet.PacketMobVelocity;
import mapmakingtools.network.packet.PacketOpenFilterMenuClientServer;
import mapmakingtools.network.packet.PacketOpenFilterMenuServerClient;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import mapmakingtools.network.packet.PacketPotionSpawner;
import mapmakingtools.network.packet.PacketQuickBuild;
import mapmakingtools.network.packet.PacketRemoveEntityFromWorld;
import mapmakingtools.network.packet.PacketSignEdit;
import mapmakingtools.network.packet.PacketSkullModify;
import mapmakingtools.network.packet.PacketSpawnerTimings;
import mapmakingtools.network.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.network.packet.PacketVillagerShop;
import mapmakingtools.network.packet.PacketWrenchTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author ProPercivalalb
 */
public enum PacketTypeHandler {
	
	SKULL_MODIFY(PacketSkullModify.class),
	OPEN_ITEM_EDITOR(PacketOpenItemEditor.class),
	ITEM_EDITOR_UPDATE(PacketItemEditorUpdate.class),
	BUILD_LEFT_CLICK(PacketBuildLeftClick.class),
	BUILD_RIGHT_CLICK(PacketBuildRightClick.class),
	QUICK_BUILD(PacketQuickBuild.class),
	FLY(PacketFly.class),
	BEACON_MODIFY(PacketBeaconModify.class),
	FILL_INVENTORY(PacketFillInventory.class),
	CONVERT_TO_DROPPER(PacketConvertToDropper.class),
	CONVERT_TO_DISPENSER(PacketConvertToDispenser.class),
	MOB_TYPE(PacketMobType.class),
	BABY_MONSTER(PacketBabyMonster.class),
	CREEPER_PROPERTIES(PacketCreeperProperties.class),
	SPAWNER_TIMINGS(PacketSpawnerTimings.class),
	MOB_VELOCITY(PacketMobVelocity.class),
	MOB_POSITION(PacketMobPosition.class),
	FITLER_MENU(PacketOpenFilterMenuClientServer.class),
	FITLER_MENU_NBT_UPDATE(PacketOpenFilterMenuServerClient.class),
	FILTER_PAGE_MENU(PacketFilterPageMenu.class),
	MOB_ARMOR(PacketMobArmor.class),
	POTION_SPAWNER(PacketPotionSpawner.class),
	ITEM_SPAWNER(PacketItemSpawner.class),
	VILLAGER_SHOP(PacketVillagerShop.class),
	MAX_VILLAGER_RECIPES(PacketMaxVillagerRecipes.class),
	WRENCH_TASK(PacketWrenchTask.class),
	VILLAGER_RECIPE_AMOUNR(PacketVillagerRecipeAmounts.class),
	CHEST_SYMMETRIFY(PacketChestSymmetrify.class),
	REMOVE_ENTITY_FROM_WORLD(PacketRemoveEntityFromWorld.class),
	SIGN_EDIT(PacketSignEdit.class),
	BIOME_UPDATE(PacketBiomeUpdate.class),
	COMMAND_BLOCK_NAME(PacketCommandBlockAlias.class),
	FRAME_DROP_CHANCE(PacketFrameDropChance.class),
	CHANGE_WATCH_PLAYER(PacketChangeWatchPlayer.class);
	
    private Class<? extends PacketMMT> clazz;

    PacketTypeHandler(Class<? extends PacketMMT> clazz) {
        this.clazz = clazz;
    }

    public static PacketMMT buildPacket(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        int selector = bis.read();
        DataInputStream dis = new DataInputStream(bis);
        PacketMMT packet = null;
        try {
            packet = values()[selector].clazz.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        packet.readPopulate(dis);
        return packet;
    }

    public static PacketMMT buildPacket(PacketTypeHandler type) {
        PacketMMT packet = null;
        try {
            packet = values()[type.ordinal()].clazz.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

    public static Packet populatePacket(PacketMMT packetMMT) {
        byte[] data = packetMMT.populate();
        Packet250CustomPayload packet250 = new Packet250CustomPayload();
        packet250.channel = Reference.CHANNEL_NAME;
        packet250.data = data;
        packet250.length = data.length;
        packet250.isChunkDataPacket = packetMMT.isChunkDataPacket;
        return packet250;
    }
    
    public static void populatePacketAndSendToServer(PacketMMT packetMMT) {
    	Packet packet = populatePacket(packetMMT);
    	Side side = FMLCommonHandler.instance().getEffectiveSide();
	    if(side == Side.CLIENT) {
	    	net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
	    	mc.getNetHandler().addToSendQueue(packet);
	    }
    }
    
    public static void populatePacketAndSendToClient(PacketMMT packetMMT, EntityPlayerMP player) {
    	Packet packet = populatePacket(packetMMT);
    	Side side = FMLCommonHandler.instance().getEffectiveSide();
	    if(side == Side.SERVER) {
	    	MinecraftServer server = MinecraftServer.getServer();
	    	player.playerNetServerHandler.sendPacketToPlayer(packet);
	    }
    }
}