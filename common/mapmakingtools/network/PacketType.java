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
