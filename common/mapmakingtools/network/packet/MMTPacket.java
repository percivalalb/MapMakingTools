package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public abstract class MMTPacket {
	
	public abstract void read(DataInputStream dis) throws IOException;
	public abstract void write(DataOutputStream dos) throws IOException;
	
	public abstract void execute(EntityPlayer player);
}
