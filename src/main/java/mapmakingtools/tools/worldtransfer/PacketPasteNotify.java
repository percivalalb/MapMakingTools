package mapmakingtools.tools.worldtransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.enums.Rotation;
import mapmakingtools.network.IPacket;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.item.nbt.SkullNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class PacketPasteNotify extends IPacket {

	public String name;
	
	public PacketPasteNotify() {}
	public PacketPasteNotify(String name) {
		this.name = name;
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		this.name = data.readUTF();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeUTF(this.name);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(!WorldTransferList.hasName(this.name))
			return;
		
		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketPaste(this.name, WorldTransferList.getAreaFromName(this.name)));
	}

}
