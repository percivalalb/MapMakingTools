package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketCreeperProperties extends PacketMMT {

	public int x, y, z;
	public String fuse, radius;
	
	public PacketCreeperProperties() {
		super(PacketTypeHandler.CREEPER_PROPERTIES, false);
	}
	
	public PacketCreeperProperties(int x, int y, int z, String fuse, String radius) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.fuse = fuse;
		this.radius = radius;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.fuse = data.readUTF();
		this.radius = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeUTF(fuse);
		dos.writeUTF(radius);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				int fuseTime = 30;
				int explosionRadius = 3;
				try {
					fuseTime = new Integer(fuse);
				}
				catch(Exception e) {
					player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.creeperExplosion.notInt"));
					return;
				}
				try {
					explosionRadius = new Integer(radius);
				}
				catch(Exception e) {
					player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.creeperExplosion.notInt"));
					return;
				}
				SpawnerHelper.setCreeperFuse(spawner, fuseTime);
				SpawnerHelper.setCreeperExplosionRadius(spawner, explosionRadius);				
				
				player.sendChatToPlayer(ChatMessageComponent.func_111077_e("filter.creeperExplosion.complete"));
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.func_111077_e("advMode.creativeModeNeed"));
		}
	}
}
