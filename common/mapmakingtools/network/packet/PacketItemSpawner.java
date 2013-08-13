package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.filters.server.FilterServerItemSpawner;
import mapmakingtools.filters.server.FilterServerMobArmor;
import mapmakingtools.filters.server.FilterServerPotionSpawner;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketItemSpawner extends PacketMMT {

	public int x, y, z;
	
	public PacketItemSpawner() {
		super(PacketTypeHandler.ITEM_SPAWNER, false);
	}
	
	public PacketItemSpawner(int x, int y, int z) {
		this();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			if(playerMP.openContainer != null && playerMP.openContainer instanceof ContainerFilter) {
				ContainerFilter container = (ContainerFilter)playerMP.openContainer;
				if(container.current != null && container.current instanceof FilterServerItemSpawner) {
					FilterServerItemSpawner armor = (FilterServerItemSpawner)container.current;
					if(armor == null) return;
					SpawnerHelper.setItemType(player.worldObj.getBlockTileEntity(x, y, z), container.getSlot(0).getStack());
					player.sendChatToPlayer(ChatMessageComponent.func_111082_b("filter.itemSpawner.complete", new Object[] {container.getSlot(0).getStack() == null ? "null" : container.getSlot(0).getStack().getDisplayName()}));
				}
			}
		}
	}

}
