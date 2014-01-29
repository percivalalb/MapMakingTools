package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.network.packet.MMTPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.FillInventoryServerFilter;
import mapmakingtools.tools.filter.MobArmorServerFilter;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.WeightedRandom;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmorRemoveIndex extends MMTPacket {

	public int x, y, z;
	public int minecartIndex;
	
	public PacketMobArmorRemoveIndex() {}
	public PacketMobArmorRemoveIndex(int x, int y, int z, int minecartIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.minecartIndex = data.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
		dos.writeInt(minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.func_147438_o(x, y, z);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;

				List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
				WeightedRandomMinecart minecart = minecarts.get(this.minecartIndex);
				minecarts.remove(this.minecartIndex);
				if(spawner.func_145881_a().getRandomMinecart() == minecart)
					spawner.func_145881_a().setRandomMinecart((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.getRandomItem(spawner.func_145881_a().getSpawnerWorld().rand, minecarts));
				SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
					
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobArmor.removeIndex");
				chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
				player.func_145747_a(chatComponent);
			}
		}
	}

}
