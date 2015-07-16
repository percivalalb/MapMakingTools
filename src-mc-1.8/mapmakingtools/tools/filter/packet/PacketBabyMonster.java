package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketBabyMonster extends IPacketPos {

	public boolean baby;
	public int minecartIndex;
	
	public PacketBabyMonster() {}
	public PacketBabyMonster(BlockPos pos, boolean baby, int minecartIndex) {
		super(pos);
		this.baby = baby;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		super.read(packetbuffer);
		this.baby = packetbuffer.readBoolean();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		super.write(packetbuffer);
		packetbuffer.writeBoolean(this.baby);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			SpawnerUtil.setBabyMonster(spawner.getSpawnerBaseLogic(), this.baby, this.minecartIndex);
			SpawnerUtil.sendSpawnerPacketToAllPlayers(spawner);
			
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.babymonster.complete");
			chatComponent.getChatStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

}
