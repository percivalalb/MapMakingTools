package mapmakingtools.handler;

import org.lwjgl.opengl.GL11;

import mapmakingtools.ModItems;
import mapmakingtools.lib.Constants;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class WorldOverlayHandler {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	private static boolean hasCheckedVersion = false;
	
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		if(!PlayerAccess.canEdit(mc.player) || !ClientData.playerData.hasSelectedPoints() || !(mc.player.getHeldItemMainhand().getItem() == ModItems.EDIT_ITEM && mc.player.getHeldItemMainhand().getMetadata() == 0))
			return;
		GlStateManager.pushMatrix();
		PlayerData data = ClientData.playerData;
		
		int minX = data.getMinX();
		int minY = data.getMinY();
		int minZ = data.getMinZ();
		int maxX = data.getMaxX() + 1;
		int maxY = data.getMaxY() + 1;
		int maxZ = data.getMaxZ() + 1;
		 
		AxisAlignedBB boundingBox = new AxisAlignedBB(data.getMinPos(), data.getMaxPos().add(1, 1, 1));
		this.drawSelectionBox(mc.player, event.getPartialTicks(), boundingBox);
		GlStateManager.popMatrix();
	}

	public void drawSelectionBox(EntityPlayer player, float particleTicks, AxisAlignedBB boundingBox) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING); //Make the line see thought blocks
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST); //Make the line see thought blocks
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
        //TODO Used when drawing outline of bounding box 
        GL11.glLineWidth(2.0F);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    	double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)particleTicks;
    	double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)particleTicks;
    	double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)particleTicks;
    	
        if(Constants.RENDER_ALL_BLOCKS) {
        	int minX = (int)boundingBox.minX;
        	int minY = (int)boundingBox.minY;
        	int minZ = (int)boundingBox.minZ;
        	int maxX = (int)boundingBox.maxX;
        	int maxY = (int)boundingBox.maxY;
   		 	int maxZ = (int)boundingBox.maxZ;
   		 	for(int x = minX; x < maxX; ++x) {
   		 		for(int y = minY; y < maxY; ++y) {
   		 			for(int z = minZ; z < maxZ; ++z) {
   		 				AxisAlignedBB smallBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
   		 				RenderGlobal.drawSelectionBoundingBox(smallBox.offset(-d0, -d1, -d2), 1F, 1F, 1F, 1F);
   		 			} 
   		 		}
   		 	}
        } 
        else {
        	RenderGlobal.drawSelectionBoundingBox(boundingBox.offset(-d0, -d1, -d2), 1F, 1F, 1F, 1F);
        	if(Constants.RENDER_SELECTED_POSITION) {
	        	PlayerData data = ClientData.playerData;
	        	RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(data.getFirstPoint(), data.getFirstPoint().add(1, 1, 1)).offset(-d0, -d1, -d2), 1F, 1F, 0F, 0.8F);
	        	RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(data.getSecondPoint(), data.getSecondPoint().add(1, 1, 1)).offset(-d0, -d1, -d2), 0F, 1F, 1F, 0.8F);
        	}
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST); //Make the line see thought blocks
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING); //Make the line see thought blocks
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
	
	/**
	public void drawBoundingBox(AxisAlignedBB boundingBox) {
		Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    tessellator.draw();
	}**/
}
