package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateSpawner;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketBabyMonster extends AbstractServerMessage {

	public BlockPos pos;
	public boolean baby;
	public int minecartIndex;
	
	public PacketBabyMonster() {}
	public PacketBabyMonster(BlockPos pos, boolean baby, int minecartIndex) {
		this.pos = pos;
		this.baby = baby;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = BlockPos.fromLong(packetbuffer.readLong());
		this.baby = packetbuffer.readBoolean();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeLong(this.pos.toLong());
		packetbuffer.writeBoolean(this.baby);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public IMessage process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return null;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			SpawnerUtil.setBabyMonster(spawner.func_145881_a(), this.baby, this.minecartIndex);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.babymonster.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			return new PacketUpdateSpawner(spawner, pos);
		}
		
		return null;
	}

}
