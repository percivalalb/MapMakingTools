package mapmakingtools.handler;

import java.util.Iterator;
import java.util.List;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 **/
public class ScreenRenderHandler {
	
	private int INDEX_HISTORY = 1;
	private boolean hasButtonBeenUp = true;
	RenderItem renderer = new RenderItem();
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		int mouseX = event.mouseX;
		int mouseY = event.mouseY;
		ElementType type = event.type;
		
	    if (PlayerAccess.canSeeBlockIdHelper(ClientHelper.mc.thePlayer)) {
	    	if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
    			
	    		if(ClientHelper.mc.currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat)ClientHelper.mc.currentScreen;
	    			int chatPostion = ReflectionHelper.getField(GuiChat.class, GuiTextField.class, chat, 8).getCursorPosition();
	    			boolean isHovering = false;
	    			
	    			ScaledResolution scaledresolution = new ScaledResolution(ClientHelper.mc.gameSettings, ClientHelper.mc.displayWidth, ClientHelper.mc.displayHeight);
	    			int width = scaledresolution.getScaledWidth();
	    			int height = scaledresolution.getScaledHeight();
	    			
	    			int rowCount = 1;
	    			int columnCount = 1;
	    			
	    			
	    			
	    			
	    			GL11.glPushMatrix();
	    			//GL11.glEnable(GL11.GL_SCISSOR_TEST);
	    			//GL11.glScissor(60, 60, 1000, 100);
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    			drawRect(0, 0, 100, 100, 12040119);
	    			//GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    			GL11.glPopMatrix();
	    			
		    		if(!isHovering) {
		    			//drawHoveringText(Arrays.asList(EnumChatFormatting.AQUA + "Block Id Helper", "Right click on a block", "to insert its id into", "the chat bar below!"), xOffset - 10, (int)(numberOfRows * (18.8D * scale)) + 19, mc.fontRenderer);	
		    		}
	            }
	    	}
	    }
	}
	
	public void drawHoveringText(List par1List, int mouseX, int mouseY, int width, int height, FontRenderer font) {
        if (!par1List.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = mouseX + 12;
            int j1 = mouseY - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > height)
            {
                j1 = height - k1 - 6;
            }

            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String)par1List.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
	
	 protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
	    {
	        float f = (float)(par5 >> 24 & 255) / 255.0F;
	        float f1 = (float)(par5 >> 16 & 255) / 255.0F;
	        float f2 = (float)(par5 >> 8 & 255) / 255.0F;
	        float f3 = (float)(par5 & 255) / 255.0F;
	        float f4 = (float)(par6 >> 24 & 255) / 255.0F;
	        float f5 = (float)(par6 >> 16 & 255) / 255.0F;
	        float f6 = (float)(par6 >> 8 & 255) / 255.0F;
	        float f7 = (float)(par6 & 255) / 255.0F;
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
	        Tessellator tessellator = Tessellator.instance;
	        tessellator.startDrawingQuads();
	        tessellator.setColorRGBA_F(f1, f2, f3, f);
	        tessellator.addVertex((double)par3, (double)par2, (double)300.0F);
	        tessellator.addVertex((double)par1, (double)par2, (double)300.0F);
	        tessellator.setColorRGBA_F(f5, f6, f7, f4);
	        tessellator.addVertex((double)par1, (double)par4, (double)300.0F);
	        tessellator.addVertex((double)par3, (double)par4, (double)300.0F);
	        tessellator.draw();
	        GL11.glShadeModel(GL11.GL_FLAT);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
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
