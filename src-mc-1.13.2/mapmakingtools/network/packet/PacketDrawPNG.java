package mapmakingtools.network.packet;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Supplier;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldAction;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.datareader.BlockColourList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author ProPercivalalb
 */
public class PacketDrawPNG {

	private BufferedImage bufferedimage;
	private EnumFacing facing;
	private int scalingType;
	
	public PacketDrawPNG() {}
	public PacketDrawPNG(BufferedImage bufferedimage) {
		this.bufferedimage = bufferedimage;
	}
	
	public PacketDrawPNG(BufferedImage bufferedimage, EnumFacing facing, int scalingType) {
		this.bufferedimage = bufferedimage;
		this.facing = facing;
		this.scalingType = scalingType;
	}
	
	public static void encode(PacketDrawPNG msg, PacketBuffer buf) {
		buf.writeInt(msg.bufferedimage.getWidth());
		buf.writeInt(msg.bufferedimage.getHeight());
		
		for(int i = 0; i < msg.bufferedimage.getWidth(); i++) {
        	for(int j = 0; j < msg.bufferedimage.getHeight(); j++) {
        		buf.writeInt(msg.bufferedimage.getRGB(i, j));
        	}
		}
	}
	
	public static PacketDrawPNG decode(PacketBuffer buf) {
		int width = buf.readInt();
		int height = buf.readInt();
		BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		for(int i = 0; i < width; i++) {
        	for(int j = 0; j < height; j++) {
        		bufferedimage.setRGB(i, j, buf.readInt());
        	}
		}
		return new PacketDrawPNG(bufferedimage);
	}
	
	public static class Handler {
        public static void handle(final PacketDrawPNG msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		World world = player.world;
        		PlayerData data = WorldData.getPlayerData(player);
        		int[] selection = data.getSelectionSize();
        		int transformType = 1;
        		EnumFacing facing = EnumFacing.WEST;
        		if((facing.getAxis() == EnumFacing.Axis.X && selection[0] != 1) || facing.getAxis() == EnumFacing.Axis.Z && selection[2] != 1) {
        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.drawpng.facenot1depth");
        			chatComponent.getStyle().setColor(TextFormatting.RED);
        			player.sendMessage(chatComponent);
        			return;
        		}
        		
        		
        		EnumFacing[] drawDirections = new EnumFacing[2];
        		drawDirections[0] = EnumFacing.byHorizontalIndex(facing.getHorizontalIndex() + 3); 
        		drawDirections[1] = EnumFacing.DOWN;
        		

        		// Warn about scaling
        		boolean shouldScale = selection[drawDirections[0].getAxis() == EnumFacing.Axis.X ? 0 : 2] % msg.bufferedimage.getWidth() != 0 || selection[1] % msg.bufferedimage.getHeight() != 0;
        		BufferedImage imageToDraw = msg.bufferedimage;
        		
        		if(shouldScale) {
        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.drawpng.scaling.args", transformType);
        			chatComponent.getStyle().setColor(TextFormatting.RED);
        			player.sendMessage(chatComponent);
        		
        			double scaleX = selection[drawDirections[0].getAxis() == EnumFacing.Axis.X ? 0 : 2] / (double)msg.bufferedimage.getWidth();
        			double scaleY = selection[1] / (double)msg.bufferedimage.getHeight();
        			
        			BufferedImage after = new BufferedImage(selection[drawDirections[0].getAxis() == EnumFacing.Axis.X ? 0 : 2], selection[1], BufferedImage.TYPE_INT_ARGB);
        			AffineTransform at = new AffineTransform();
        			at.scale(scaleX, scaleY);
        			AffineTransformOp scaleOp = new AffineTransformOp(at, transformType);
        			after = scaleOp.filter(msg.bufferedimage, after);
        			imageToDraw = after;
        		}
        		
        		int x = data.getMinX();
        		int z = data.getMinZ();
        		if(drawDirections[0] == EnumFacing.WEST) {
        			x = data.getMaxX();
        		}
        		else if(drawDirections[0] == EnumFacing.EAST) {
        			x = data.getMinX();
        		}
        		else if(drawDirections[0] == EnumFacing.NORTH) {
        			z = data.getMaxZ();
        		}
        		else if(drawDirections[0] == EnumFacing.SOUTH) {
        			z = data.getMinZ();
        		}
        		
        		
        		BlockPos basePos = new BlockPos(x, data.getMaxY(), z);
        		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
        		int blocks = 0;
        		
                for(int i = 0; i < imageToDraw.getWidth(); i++) {
                    for(int j = 0; j < imageToDraw.getHeight(); j++) {
                    	BlockPos newPos = basePos.offset(drawDirections[0], i).offset(drawDirections[1], j);
          
                    	int[] rgb = BlockColourList.getPixelData(imageToDraw, i, j);
        	            Object[] closest = BlockColourList.closestMaterial(rgb[0], rgb[1], rgb[2]);
                        	
        	                
        	            list.add(BlockCache.createCache(player, world, newPos));
        				blocks += 1;
        	            
        	            if(rgb[3] < 1)
        	            	WorldAction.setBlock(world, newPos, Blocks.AIR.getDefaultState(), false);
        	            else
        	            	WorldAction.setBlock(world, newPos, ForgeRegistries.BLOCKS.getValue(new ResourceLocation((String)closest[0])).getStateById((Integer)closest[1]), false);
            				
             	            
                    }
                }
                
                data.getActionStorage().addUndo(list);
                
                TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.drawpng.complete", "" + blocks);
        		chatComponent.getStyle().setItalic(true);
        		player.sendMessage(chatComponent);
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
