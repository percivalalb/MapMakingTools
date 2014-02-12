package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.network.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class PacketTest extends IPacket {
	
	public PacketTest() {}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		
	}

	@Override
	public void execute(EntityPlayer player) {
		FMLLog.info("Yay");
	}

}
