package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.item.nbt.SkullNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketSkullModify extends AbstractServerMessage {

	public String name;
	
	public PacketSkullModify() {}
	public PacketSkullModify(String name) {
		this.name = name;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readStringFromBuffer(32);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.name);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
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
