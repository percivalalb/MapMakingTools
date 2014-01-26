package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class PacketTest extends MMTPacket {
	
	public PacketTest() {}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		
	}

	@Override
	public void write(DataOutputStream dos) throws IOException{
		
	}

	@Override
	public void execute(EntityPlayer player) {
		FMLLog.info("Yay");
	}

}
