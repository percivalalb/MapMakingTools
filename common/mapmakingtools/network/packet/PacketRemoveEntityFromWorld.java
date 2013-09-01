package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.filters.server.FilterServerMobArmor;
import mapmakingtools.filters.server.FilterServerPotionSpawner;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketRemoveEntityFromWorld extends PacketMMT {

	public int entityId;
	
	public PacketRemoveEntityFromWorld() {
		super(PacketTypeHandler.REMOVE_ENTITY_FROM_WORLD, false);
	}
	
	public PacketRemoveEntityFromWorld(int entityId) {
		this();
		this.entityId = entityId;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(entityId);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		Entity entity = player.worldObj.getEntityByID(entityId);
		if(entity != null)
			player.worldObj.removeEntity(player.worldObj.getEntityByID(entityId));
	}

}
