package mapmakingtools.network.packet;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class PacketRequestPNG {

	public String location;
	
	public PacketRequestPNG(String location) {
		this.location = location;
	}

	public static void encode(PacketRequestPNG msg, PacketBuffer buf) {
		buf.writeString(msg.location);
	}
	
	public static PacketRequestPNG decode(PacketBuffer buf) {
		String location = buf.readString(Short.MAX_VALUE);
		return new PacketRequestPNG(location);
	}
	
	public static class Handler {
        public static void handle(final PacketRequestPNG msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		ResourceLocation resourceLoc = new ResourceLocation("textures/items/"+msg.location+".png");
        		
        		IResource iresource;
        		try {
        			iresource = Minecraft.getInstance().getResourceManager().getResource(resourceLoc);
                    ByteBuffer bufferedimage = TextureUtil.readToNativeBuffer(iresource.getInputStream());
            		
            		//TODO PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketDrawPNG(bufferedimage));
        		}
        		catch(Exception e) {
        			e.printStackTrace();
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
