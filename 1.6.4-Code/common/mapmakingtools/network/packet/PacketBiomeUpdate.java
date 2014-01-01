package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.chunk.Chunk;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketBiomeUpdate extends PacketMMT {

	public int x, z;
	public int biomeId;
	
	public PacketBiomeUpdate() {
		super(PacketTypeHandler.BIOME_UPDATE, true);
	}
	
	public PacketBiomeUpdate(int x, int z, int biomeId) {
		this();
		this.x = x;
		this.z = z;
		this.biomeId = biomeId;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.z = data.readInt();
		this.biomeId = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(z);
		dos.writeInt(biomeId);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			Chunk chunk = player.worldObj.getChunkFromBlockCoords(x, z);
			byte[] biomes = chunk.getBiomeArray();
			biomes[((z & 0xF) << 4 | x & 0xF)] = (byte)biomeId;
			chunk.setBiomeArray(biomes);
			chunk.setChunkModified();
			int y = player.worldObj.getTopSolidOrLiquidBlock(x, z);
			player.worldObj.markBlockForUpdate(x, y, z);
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}

}
