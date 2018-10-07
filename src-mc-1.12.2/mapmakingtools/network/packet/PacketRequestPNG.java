package mapmakingtools.network.packet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.datareader.BlockColourList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import scala.actors.threadpool.Arrays;

/**
 * @author ProPercivalalb
 */
public class PacketRequestPNG extends AbstractClientMessage {

	public String location;
	
	public PacketRequestPNG() {}
	public PacketRequestPNG(String location) {
		this.location = location;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.location = packetbuffer.readString(Short.MAX_VALUE);
	}
	
	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.location);
	}
	
	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		ResourceLocation resourceLoc = new ResourceLocation("textures/items/"+this.location+".png");
		
		IResource iresource;
		try {
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLoc);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
    		
    		PacketDispatcher.sendToServer(new PacketDrawPNG(bufferedimage));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
