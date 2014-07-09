package mapmakingtools.network;

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
import mapmakingtools.tools.filter.packet.PacketBabyMonster;
import mapmakingtools.tools.filter.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.tools.filter.packet.PacketVillagerShop;

/**
 * @author ProPercivalalb
 */
public enum PacketType {

	SET_POINT1(PacketSetPoint1.class),
	SET_POINT2(PacketSetPoint2.class),
	UPDATE_BLOCK(PacketUpdateBlock.class),
	UPDATE_ENTITY(PacketUpdateEntity.class),
	EDIT_BLOCK(PacketEditBlock.class),
	EDIT_ENTITY(PacketEditEntity.class),
	OPEN_ITEM_EDITOR(PacketOpenItemEditor.class),
	UPDATE_ITEM_EDITOR(PacketItemEditorUpdate.class),
	SELECTED_FILTER(PacketSelectedFilter.class),
	PHANTOM_INFINITY(PacketPhantomInfinity.class),
	FILL_INVENTORY(PacketFillInventory.class),
	MOB_TYPE(PacketMobType.class),
	MOB_ARMOR(PacketMobArmor.class),
	MOB_ARMOR_UPDATE(PacketMobArmorUpdate.class),
	MOB_ARMOR_REMOVE_INDEX(PacketMobArmorRemoveIndex.class),
	MOB_ARMOR_ADD_INDEX(PacketMobArmorAddIndex.class),
	CUSTOM_GIVE(PacketCustomGive.class),
	COMMAND_BLOCK_ALIAS(PacketCommandBlockAlias.class),
	ITEM_SPAWNER(PacketItemSpawner.class),
	MOB_POSITION(PacketMobPosition.class),
	SPAWNER_TIMINGS(PacketSpawnerTimings.class),
	MOB_VELOCITY(PacketMobVelocity.class),
	BABY_CREATURE(PacketBabyMonster.class),
	CREEPER_PROPERTIES(PacketCreeperProperties.class),
	VILLAGER_RECIPE_AMOUNTS(PacketVillagerRecipeAmounts.class),
	VILLAGER_SHOP(PacketVillagerShop.class),
	CHEST_SYMMETRITY(PacketChestSymmetrify.class),
	CONVERT_TO_DISPENSER(PacketConvertToDispenser.class),
	CONVER_TO_DROPPER(PacketConvertToDropper.class),
	SKULL_OWNER(PacketSkullModify.class);
	
	public Class<? extends IPacket> packetClass;
	
	PacketType(Class<? extends IPacket> packetClass) {
		this.packetClass = packetClass;
	}
	
	public IPacket createInstance() throws Exception {
		return this.packetClass.newInstance();
	}
	
	public static byte getIdFromClass(Class<? extends IPacket> packetClass) {
		for(PacketType type : values())
			if(type.packetClass == packetClass)
				return (byte)type.ordinal();
		return -1;
	}
}
