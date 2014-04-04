package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatComponentTranslation;

public class PacketCommandBlockAlias extends IPacket {

	public int x, y, z;
	public String name;
	
	public PacketCommandBlockAlias() {}
	public PacketCommandBlockAlias(int x, int y, int z, String name) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.name = data.readUTF();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		data.writeUTF(this.name);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;

		TileEntity tile = player.worldObj.getTileEntity(this.x, this.y, this.z);
		if(tile instanceof TileEntityCommandBlock) {
			TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
			commandBlock.func_145993_a().func_145754_b(this.name);
			if(!player.worldObj.isRemote) { 
				//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.commandBlockName.complete", name));
				if(ServerHelper.isServer()) {
	    			MinecraftServer server = MinecraftServer.getServer();
	    			server.getConfigurationManager().sendPacketToAllPlayersInDimension(commandBlock.getDescriptionPacket(), commandBlock.getWorldObj().provider.dimensionId);
    			}
    			
    			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.commandblockalias.complete", this.name);
				chatComponent.getChatStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			}
		}
	}
}
