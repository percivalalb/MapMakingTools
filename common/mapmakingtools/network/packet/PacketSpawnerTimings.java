package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.FilterHelper;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketSpawnerTimings extends PacketMMT {

	public int x, y, z;
	public String minDelay, maxDelay, spawnRadius, spawnCount, entityCap, detectionRange;
	
	public PacketSpawnerTimings() {
		super(PacketTypeHandler.SPAWNER_TIMINGS, false);
	}
	
	public PacketSpawnerTimings(int x, int y, int z, String minDelay, String maxDelay, String spawnRadius, String spawnCount, String entityCap, String detectionRange) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.spawnRadius = spawnRadius;
		this.spawnCount = spawnCount;
		this.entityCap = entityCap;
		this.detectionRange = detectionRange;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.minDelay = data.readUTF();
		this.maxDelay = data.readUTF();
		this.spawnRadius = data.readUTF();
		this.spawnCount = data.readUTF();
		this.entityCap = data.readUTF();
		this.detectionRange = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeUTF(minDelay);
		dos.writeUTF(maxDelay);
		dos.writeUTF(spawnRadius);
		dos.writeUTF(spawnCount);
		dos.writeUTF(entityCap);
		dos.writeUTF(detectionRange);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				int minDelayNO;
				int maxDelayNO;
				int spawnRadiusNO;
				int spawnCountNO;
				int entityCapNO;
				int detectionRadiusNO;
				if(!FilterHelper.isNumber(minDelay) || !FilterHelper.isNumber(maxDelay) || !FilterHelper.isNumber(spawnRadius) || !FilterHelper.isNumber(spawnCount) || !FilterHelper.isNumber(entityCap) || !FilterHelper.isNumber(detectionRange)) {
					player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.creeperExplosion.notInt"));
					return;
				}
				minDelayNO = FilterHelper.getNumber(minDelay);
				maxDelayNO = FilterHelper.getNumber(maxDelay);
				spawnRadiusNO = FilterHelper.getNumber(spawnRadius);
				spawnCountNO = FilterHelper.getNumber(spawnCount);
				entityCapNO = FilterHelper.getNumber(entityCap);
				detectionRadiusNO = FilterHelper.getNumber(detectionRange);
				SpawnerHelper.setMinDelay(tile, minDelayNO);
				SpawnerHelper.setMaxDelay(tile, maxDelayNO);
				SpawnerHelper.setSpawnRadius(tile, spawnRadiusNO);
				SpawnerHelper.setSpawnCount(tile, spawnCountNO);
				SpawnerHelper.setEntityCap(tile, entityCapNO);
				SpawnerHelper.setDetectionRadius(tile, detectionRadiusNO);
				
				player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.spawnerTimings.complete"));
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}
}
