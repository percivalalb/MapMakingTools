package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mapmakingtools.MapMakingTools;
import mapmakingtools.core.helper.ChestSymmetrifyHelper;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.network.PacketTypeHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.chunk.Chunk;

/**
 * @author ProPercivalalb
 */
public class PacketSignEdit extends PacketMMT {

	public int x, y, z;
	public String[] signLines;
	
	public PacketSignEdit() {
		super(PacketTypeHandler.SIGN_EDIT, true);
		this.signLines = new String[4];
	}
	
	public PacketSignEdit(int x, int y, int z, String[] par4ArrayOfStr) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.signLines = new String[] {par4ArrayOfStr[0], par4ArrayOfStr[1], par4ArrayOfStr[2], par4ArrayOfStr[3]};
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
        for (int i = 0; i < 4; ++i) {
            this.signLines[i] = Packet.readString(data, 15);
        }
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		for (int i = 0; i < 4; ++i) {
	        Packet.writeString(this.signLines[i], dos);
	    }
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(GeneralHelper.inCreative(player)) {
			TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
			if(tile instanceof TileEntitySign) {
				TileEntitySign sign = (TileEntitySign)tile;
				sign.signText = signLines;
				Chunk chunk = sign.worldObj.getChunkFromBlockCoords(x, z);
				if(chunk != null) {
					chunk.setChunkModified();
				}
				
				if(!player.worldObj.isRemote) { 
					player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("filter.signEdit.complete"));
					MinecraftServer server = MinecraftServer.getServer();
					server.getConfigurationManager().sendToAllNearExcept(player, x + 0.5D, y + 0.5D, z + 0.5D, 256 * 256, sign.worldObj.provider.dimensionId, sign.getDescriptionPacket());
				}
			}
		}
		else {
			if(!player.worldObj.isRemote) 
				player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
		}
	}
}
