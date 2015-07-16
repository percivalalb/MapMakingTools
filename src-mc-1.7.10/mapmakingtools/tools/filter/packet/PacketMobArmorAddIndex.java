package mapmakingtools.tools.filter.packet;

import java.io.IOException;
import java.util.List;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmorAddIndex extends IPacketPos {

	public int minecartIndex;
	
	public PacketMobArmorAddIndex() {}
	public PacketMobArmorAddIndex(BlockPos pos, int minecartIndex) {
		super(pos);
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeInt(minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
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
