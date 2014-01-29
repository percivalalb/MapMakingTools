package mapmakingtools.network;

import mapmakingtools.network.packet.*;
import mapmakingtools.tools.filter.packet.*;

/**
 * @author ProPercivalalb
 */
public enum PacketType {

	SET_POINT1(PacketSetPoint1.class),
	SET_POINT2(PacketSetPoint2.class),
	TEST(PacketTest.class),
	UPDATE_BLOCK(PacketUpdateBlock.class),
	UPDATE_ENTITY(PacketUpdateEntity.class),
	EDIT_BLOCK(PacketEditBlock.class),
	EDIT_ENTITY(PacketEditEntity.class),
	SELECTED_FILTER(PacketSelectedFilter.class),
	PHANTOM_INFINITY(PacketPhantomInfinity.class),
	FILL_INVENTORY(PacketFillInventory.class),
	MOB_TYPE(PacketMobType.class),
	MOB_ARMOR(PacketMobArmor.class),
	MOB_ARMOR_UPDATE(PacketMobArmorUpdate.class),
	MOB_ARMOR_REMOVE_INDEX(PacketMobArmorRemoveIndex.class),
	MOB_ARMOR_ADD_INDEX(PacketMobArmorAddIndex.class);
	
	private Class<? extends MMTPacket> packetClass;
	
	PacketType(Class<? extends MMTPacket> packetClass) {
		this.packetClass = packetClass;
	}
	
	public MMTPacket createInstance() throws Exception {
		return this.packetClass.newInstance();
	}
	
	public static int getIdFromPacket(MMTPacket packet) {
		for(PacketType type : values()) {
			if(type.packetClass.equals(packet.getClass()))
				return type.ordinal();
		}
		return -1;
	}
	
	public static PacketType getPacketFromId(int id) {
		return values()[id];
	}
}
