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
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class PacketAddArea extends IPacket {

	public String name;
	public boolean override;
	public ArrayList<CachedBlock> list;
	
	public PacketAddArea() {}
	public PacketAddArea(String name, boolean override, ArrayList<CachedBlock> list) {
		this.name = name;
		this.override = override;
		this.list = list;
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		this.name = data.readUTF();
		this.override = data.readBoolean();
		this.list = new ArrayList<CachedBlock>();
		int size = data.readInt();
		for(int i = 0; i < size; ++i)
			this.list.add(new CachedBlock(data, false));
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeUTF(this.name);
		data.writeBoolean(this.override);
		data.writeInt(this.list.size());
		for(int i = 0; i < this.list.size(); ++i)
			this.list.get(i).writeToOutputStream(data, false);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		WorldTransferList.add(this.name, this.override, this.list);

		ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete", name);
		chatComponent.getChatStyle().setColor(EnumChatFormatting.GREEN);
		player.addChatMessage(chatComponent);
		chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.worldtransfer.copy.complete2", name);
		chatComponent.getChatStyle().setItalic(true);
		player.addChatMessage(chatComponent);
		
		WorldTransferList.saveToFile();
	}

}
