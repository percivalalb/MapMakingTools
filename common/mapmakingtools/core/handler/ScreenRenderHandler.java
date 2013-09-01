package mapmakingtools.core.handler;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.core.helper.IDScreenRenderHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.core.helper.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraft.client.gui.GuiChat;

/**
 * @author ProPercivalalb
 **/
public class ScreenRenderHandler {
	
	private int INDEX_HISTORY = 1;
	RenderItem renderer = new RenderItem();
	Minecraft mc = Minecraft.getMinecraft();
	
	@ForgeSubscribe
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
	    Minecraft mc = Minecraft.getMinecraft();
	    if (mc.thePlayer != null && mc.thePlayer.capabilities.isCreativeMode) {
	    	if (event.type == RenderGameOverlayEvent.ElementType.HELMET) {
	    		if(mc.currentScreen instanceof GuiChat) {
	   
	    			//this.mc.ingameGUI.getChatGUI().clearChatMessages();
	    			ScaledResolution var5 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	    			int width = var5.getScaledWidth();
	    			int height = var5.getScaledHeight();
	    			Iterator<ItemStack> ite = IDScreenRenderHelper.getIterator();
	    			int row = 0;
	    			int column = -1;
	    			//var5.getScaleFactor();
	    			for(int var1 = 0; var1 < IDScreenRenderHelper.getListSize(); ++var1) {
	    				if(column >= (width / 17)) {
	    					column = 0;
	    					++row;
	    				}
	    				else {
	    					++column;
	    				}
	    			}
	    			drawRect(4 - 1, 4 - 1, width - 4 + 1, 32 + (row * 21) + 1, -6250336);
	                drawRect(4, 4, width - 4, 32 + (row * 21), 12040119);
	                row = 0;
	    			column = -1;
	    			while(ite.hasNext()) {
	    				ItemStack stack = ite.next();
	    				if(column >= (width / 17)) {
	    					column = 0;
	    					++row;
	    				}
	    				else {
	    					++column;
	    				}
	    				GL11.glPushMatrix();
	    				GL11.glDisable(GL11.GL_DEPTH_TEST);
	                	GL11.glDepthMask(false);
	                	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	                	float scale = 0.9F; //1.0F is normal size
	            		GL11.glScalef(scale, scale, scale);
	            		RenderHelper.enableGUIStandardItemLighting();
	            		GL11.glDisable(GL11.GL_LIGHTING);
	                	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	                	GL11.glEnable(GL11.GL_COLOR_MATERIAL);
	                	GL11.glEnable(GL11.GL_LIGHTING);
	                	renderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, (column * 18) + 5, (row * 24) + 8);
	            		float fontScale = 0.55F; //1.0F is normal size
	            		GL11.glScalef(fontScale, fontScale, fontScale);
	            		mc.fontRenderer.drawString(String.valueOf(stack.itemID) + ":" + String.valueOf(stack.getItemDamage()), (int)(((column * 18) + 7) / fontScale), (int)(((row * 24) + 18) / fontScale) + 13, 0);
	            		GL11.glDisable(GL11.GL_LIGHTING);
	                	GL11.glDepthMask(true);
	                	GL11.glEnable(GL11.GL_DEPTH_TEST);
	            		GL11.glPopMatrix();
	            	}
	    		}
	    	}
	    }
	}
	
	public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6) {
		float f = 0.01390625F;
	    float f1 = 0.01390625F;
	    Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawingQuads();
	    tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double) -90.0D, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
	    tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double) -90.0D, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
	    tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double) -90.0D, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
	    tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double) -90.0D, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
	    tessellator.draw();
	}
	
	/**
	 * Renders an icon across the whole of the screen.
	 * @param time The opacity 1.0F is solid.
	 * @param width The screen width
	 * @param height The screen height
	 * @param icon The icon wanted to be displayed
	 */
    private void renderIconOverlay(float time, int width, int height, Icon icon) {
        if (time < 1.0F) {
            time *= time;
            time *= time;
            time = time * 0.8F + 0.2F;
        }
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, time);
        Minecraft.getMinecraft().func_110434_K().func_110577_a(TextureMap.field_110575_b);
        float f1 = icon.getMinU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxU();
        float f4 = icon.getMaxV();
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, (double)height, -90.0D, (double)f1, (double)f4);
        tessellator.addVertexWithUV((double)width, (double)height, -90.0D, (double)f3, (double)f4);
        tessellator.addVertexWithUV((double)width, 0.0D, -90.0D, (double)f3, (double)f2);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, (double)f1, (double)f2);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public static void drawRect(int par0, int par1, int par2, int par3, int par4)
    {
        int j1;

        if (par0 < par2)
        {
            j1 = par0;
            par0 = par2;
            par2 = j1;
        }

        if (par1 < par3)
        {
            j1 = par1;
            par1 = par3;
            par3 = j1;
        }

        float f = (float)(par4 >> 24 & 255) / 255.0F;
        float f1 = (float)(par4 >> 16 & 255) / 255.0F;
        float f2 = (float)(par4 >> 8 & 255) / 255.0F;
        float f3 = (float)(par4 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)par0, (double)par3, 0.0D);
        tessellator.addVertex((double)par2, (double)par3, 0.0D);
        tessellator.addVertex((double)par2, (double)par1, 0.0D);
        tessellator.addVertex((double)par0, (double)par1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
