package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacket;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.item.nbt.SkullNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class PacketSkullModify extends IPacket {

	public String name;
	
	public PacketSkullModify() {}
	public PacketSkullModify(String name) {
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
		ItemStack item = player.getHeldItem();
		if(item == null)
			return;
		
		if(item != null && Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(Items.skull) && item.getItemDamage() == 3) {
			SkullNBT.setSkullName(item, this.name);
		}
		
	}

}
