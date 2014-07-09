package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmorAddIndex extends IPacket {

	public int x, y, z;
	public int minecartIndex;
	
	public PacketMobArmorAddIndex() {}
	public PacketMobArmorAddIndex(int x, int y, int z, int minecartIndex) {
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
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(x);
		data.writeInt(y);
		data.writeInt(z);
		data.writeInt(minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(x, y, z);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
					
				List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
				NBTTagCompound data = new NBTTagCompound();
				data.setInteger("Weight", 1);
				data.setString("Type", "Pig");
				data.setTag("Properties", new NBTTagCompound());
				WeightedRandomMinecart randomMinecart = spawner.func_145881_a().new WeightedRandomMinecart(data);
				minecarts.add(randomMinecart);
				spawner.func_145881_a().setRandomEntity(randomMinecart);
				SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
					
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.mobArmor.addIndex");
				chatComponent.getChatStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			}
		}
	}

}
