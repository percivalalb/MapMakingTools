package mapmakingtools.core.handler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.core.helper.IDScreenRenderHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
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
	private boolean hasButtonBeenUp = true;
	RenderItem renderer = new RenderItem();
	Minecraft mc = Minecraft.getMinecraft();
	
	@ForgeSubscribe
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
	    Minecraft mc = Minecraft.getMinecraft();
	    if (mc.thePlayer != null && (mc.thePlayer.capabilities.isCreativeMode && Constants.SHOULD_SHOW_BLOCK_ID_HELPER)) {
	    	if (event.type == RenderGameOverlayEvent.ElementType.HELMET) {
	    		if(mc.currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat)mc.currentScreen;
	    			int oldPostion = ReflectionHelper.getField(GuiChat.class, GuiTextField.class, chat, 7).getCursorPosition();
	    			ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
	    			int width = scaledresolution.getScaledWidth();
	    			int height = scaledresolution.getScaledHeight();
                	float scale = 0.9F; //1.0F is normal size
                	int xOffset = (int) (((width - (width - 28)) * scale) / 2);
	    			Iterator<ItemStack> ite = IDScreenRenderHelper.getIterator();
	    			int numberOfRows = 1;
	    			int numberOfColumns = 1;
                	for(int i = 0; i < IDScreenRenderHelper.getListSize(); ++i) {
                		if(4 + i * (18 * scale) < width - (18 * scale) * 3)
                			numberOfColumns++;
                	}
                	int roundUp = IDScreenRenderHelper.getListSize() + (numberOfColumns + (IDScreenRenderHelper.getListSize() % numberOfColumns));
                	numberOfRows = roundUp / numberOfColumns;
                	//LogHelper.logDebug("Columns: " + numberOfColumns);
                	//LogHelper.logDebug("Rows: " + numberOfRows);
                    int xMouse = Mouse.getX() * width / this.mc.displayWidth;
                    int yMouse = height - Mouse.getY() * height / this.mc.displayHeight - 1;
                	
	    			drawRect(xOffset - 1, 4 - 1, (int)(xOffset + (numberOfColumns * (18 * scale))) + 6, (int)(numberOfRows * (18.8D * scale)) + 1, -6250336);
	                drawRect(xOffset, 4, (int)(xOffset + (numberOfColumns * (18 * scale))) + 5, (int)(numberOfRows * (18.8D * scale)), 12040119);
                	int index = 0;
                	int columnIndex = -1;
                	int rowIndex = 0;
    				boolean hovering = false;
                	while(ite.hasNext()) {
	                	++columnIndex;
	                	if(columnIndex >= numberOfColumns) {
	                		columnIndex = 0;
	                		rowIndex += 1;
	                	}
	    				ItemStack stack = ite.next();
		    			if(xMouse < ((((columnIndex + 1) * 18) + 3 + xOffset) * scale) 
		    			&& xMouse > ((((columnIndex) * 18) + 3 + xOffset) * scale) 
		    			&& yMouse < ((((rowIndex + 1) * 20) + 6) * scale) 
		    			&& yMouse > (((rowIndex * 20) + 6) * scale)) {
		    				hovering = true;
		    				drawRect((int)((((columnIndex) * 18) + 4 + xOffset) * scale), (int)(((rowIndex * 20) + 7) * scale), (int)((((columnIndex + 1) * 18) + 4 + xOffset) * scale), (int)((((rowIndex + 1) * 20) + 6) * scale), -1);
		    				drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + stack.getDisplayName(), EnumChatFormatting.ITALIC + String.format("%s:%s", stack.itemID, stack.getItemDamage())), xMouse, (int)(numberOfRows * (18.8D * scale)) + 19, mc.fontRenderer);
		    				if(Mouse.isButtonDown(1) && hasButtonBeenUp) {
				    			String txtToInsert = String.format((ReflectionHelper.getField(GuiChat.class, GuiTextField.class, chat, 7).getText().endsWith(" ") ? "" : " ") +"%s:%s ", stack.itemID, stack.getItemDamage());
				    			for(int i = 0; i < txtToInsert.length(); i++) {
				    				char ch = txtToInsert.charAt(i);
				    				ReflectionHelper.getField(GuiChat.class, GuiTextField.class, chat, 7).textboxKeyTyped(ch, Integer.valueOf(ch));
				    			}
				    			ReflectionHelper.getField(GuiChat.class, GuiTextField.class, chat, 7).setCursorPosition(oldPostion + txtToInsert.length());
				    			hasButtonBeenUp = false;
		    				}
		    			}
		    			if(!Mouse.isButtonDown(1)) {
		    				hasButtonBeenUp = true;
		    			}
	    				GL11.glPushMatrix();
	                	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            		GL11.glScalef(scale, scale, scale);
	            		RenderHelper.enableGUIStandardItemLighting();
	            		GL11.glDisable(GL11.GL_LIGHTING);
	                	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	                	GL11.glEnable(GL11.GL_COLOR_MATERIAL);
	                	GL11.glEnable(GL11.GL_LIGHTING);
	                	renderer.zLevel = 400F;
	                	renderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, (columnIndex * 18) + 5 + xOffset, (rowIndex * 20) + 8);
	            		float fontScale = 0.55F; //1.0F is normal size
	            		GL11.glScalef(fontScale, fontScale, fontScale);
	            		//mc.fontRenderer.drawString(String.valueOf(stack.itemID) + ":" + String.valueOf(stack.getItemDamage()), (int)(((column * 18) + 7) / fontScale), (int)(((row * 24) + 18) / fontScale) + 13, 0);
	            		RenderHelper.disableStandardItemLighting();
	            		GL11.glDisable(GL11.GL_LIGHTING);
	            		GL11.glPopMatrix();
	            	
	            		index++;
	            	}
	    			if(!hovering) {
	    				drawHoveringText(Arrays.asList(EnumChatFormatting.AQUA + "Block Id Helper", "Right click on a block", "to insert its id into", "the chat bar below!"), xOffset - 10, (int)(numberOfRows * (18.8D * scale)) + 19, mc.fontRenderer);	
	    			}
	    		}
	    	}
	    }
	}
	
	public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
        if (!par1List.isEmpty()) {
        	ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        	int width = scaledresolution.getScaledWidth();
			int height = scaledresolution.getScaledHeight();
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

            int i1 = par2 + 12;
            int j1 = par3 - 12;
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
