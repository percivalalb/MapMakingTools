package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
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

public class PacketCommandBlockAlias extends PacketMMT {

	public int x, y, z;
	public String name;
	
	public PacketCommandBlockAlias() {
		super(PacketTypeHandler.COMMAND_BLOCK_NAME, false);
	}
	
	public PacketCommandBlockAlias(int x, int y, int z, String name) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.name = data.readUTF();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeUTF(name);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntityCommandBlock) {
				TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
				commandBlock.setCommandSenderName(name);
				if(!player.worldObj.isRemote) { 
					player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.commandBlockName.complete", name));
					MinecraftServer server = MinecraftServer.getServer();
					server.getConfigurationManager().sendToAllNear(x + 0.5D, y + 0.5D, z + 0.5D, 256 * 256, commandBlock.worldObj.provider.dimensionId, commandBlock.getDescriptionPacket());
				}
			}
		}
		else {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}
}
