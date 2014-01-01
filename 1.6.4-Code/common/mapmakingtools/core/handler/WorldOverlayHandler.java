package mapmakingtools.core.handler;

import org.lwjgl.opengl.GL11;

import mapmakingtools.ModItems;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.VersionHelper;
import mapmakingtools.core.helper.VersionHelper.Type;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.item.ItemEdit;
import mapmakingtools.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * @author ProPercivalalb
 */
public class WorldOverlayHandler {
	
	Minecraft mc = Minecraft.getMinecraft();
	private static boolean hasCheckedVersion = false;
	
	@ForgeSubscribe
	 public void onWorldRenderLast(RenderWorldLastEvent event) {
		if(!hasCheckedVersion) {
			if(mc.thePlayer != null) {
				VersionHelper.checkVersion(Type.COLOURED);
				this.hasCheckedVersion = true;
			}
		}
		
		if(mc.thePlayer == null || !mc.thePlayer.capabilities.isCreativeMode || !DataStorage.hasSelectedPostions(mc.thePlayer) || (!ItemStackHelper.isItem(mc.thePlayer.getHeldItem(), Constants.QUICK_BUILD_ITEM)) || ItemEdit.isWrench(mc.thePlayer.getHeldItem())) return; 
		GL11.glPushMatrix();
		int secMinX = DataStorage.getSelectedPosFromPlayer(mc.thePlayer)[0];
		int secMinY = DataStorage.getSelectedPosFromPlayer(mc.thePlayer)[1];
		int secMinZ = DataStorage.getSelectedPosFromPlayer(mc.thePlayer)[2];
		int secMaxX = DataStorage.getSelectedPosFromPlayer(mc.thePlayer)[3];
		int secMaxY = DataStorage.getSelectedPosFromPlayer(mc.thePlayer)[4];
		int secMaxZ = DataStorage.getSelectedPosFromPlayer(mc.thePlayer)[5]; 
		int minX = MathHelper.small(secMinX, secMaxX);
		int minY = MathHelper.small(secMinY, secMaxY);
		int minZ = MathHelper.small(secMinZ, secMaxZ);
		int maxX = MathHelper.big(secMinX, secMaxX)+1;
		int maxY = MathHelper.big(secMinY, secMaxY)+1;
		int maxZ = MathHelper.big(secMinZ, secMaxZ)+1;
		 
		AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		this.drawSelectionBox(mc.thePlayer, event.partialTicks, boundingBox);
        GL11.glPopMatrix();
	}

	public void drawSelectionBox(EntityPlayer player, float particleTicks, AxisAlignedBB boundingBox)
    {
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
   		 				AxisAlignedBB smallBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
   		 				this.drawOutlinedBoundingBox(smallBox.getOffsetBoundingBox(-d0, -d1, -d2));
   		 			} 
   		 		}
   		 	}
        } 
        else {
        	this.drawOutlinedBoundingBox(boundingBox.getOffsetBoundingBox(-d0, -d1, -d2));
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST); //Make the line see thought blocks
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING); //Make the line see thought blocks
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
	
	public void drawOutlinedBoundingBox(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(3);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.draw();
        var2.startDrawing(1);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        var2.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        var2.draw();
    }
	
	public void drawBoundingBox(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.instance;
        //Bottom Face
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        //Top Facew
        tessellator.startDrawingQuads();
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.addVertex(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.addVertex(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
    }
}
