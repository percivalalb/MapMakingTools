package mapmakingtools.handler;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mapmakingtools.ModItems;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.datareader.BlockList;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

/**
 * @author ProPercivalalb
 **/
public class ScreenRenderHandler {
	
	private int INDEX_HISTORY = 1;
	private boolean hasButtonBeenUp = true;
	public boolean isHelperOpen = false;
	public RenderItem renderer = new RenderItem();
	public Field chatField = ReflectionHelper.getField(GuiChat.class, 9);
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		int mouseX = event.mouseX;
		int mouseY = event.mouseY;
		ElementType type = event.type;
		
		ItemStack stack = ClientHelper.mc.thePlayer.getHeldItem();
		
		if (event.type == RenderGameOverlayEvent.ElementType.HELMET && stack != null && stack.getItem() == ModItems.editItem && stack.getItemDamage() == 0 && PlayerAccess.canEdit(ClientHelper.mc.thePlayer)) {
    		
    		PlayerData data = ClientData.playerData;
    		FontRenderer font = ClientHelper.mc.fontRenderer;
    		GL11.glPushMatrix();
    		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            
            if(data.hasSelectedPoints()) {
        		int[] size = data.getSelectionSize();
            	font.drawStringWithShadow(String.format("Selection Size: %d * %d * %d = %d", size[0], size[1], size[2], data.getBlockCount()), 4, 4, -1);
            }
            else
            	font.drawStringWithShadow("Nothing Selected", 4, 4, -1);
            	
    		GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    		GL11.glPopMatrix();
		}
		
		
		if(Keyboard.isKeyDown(ClientHelper.mc.gameSettings.keyBindSneak.getKeyCode()) && PlayerAccess.canSeeBlockIdHelper(ClientHelper.mc.thePlayer)) {
	    	if (event.type == RenderGameOverlayEvent.ElementType.HELMET) {
	   
	    		if(ClientHelper.mc.currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat)ClientHelper.mc.currentScreen;
	    			int chatPostion = ReflectionHelper.getField(chatField, GuiTextField.class, chat).getCursorPosition();
	    			boolean isHovering = false;
	    			
	    			ScaledResolution scaling = event.resolution;
	    			int width = scaling.getScaledWidth();
	    			int height = scaling.getScaledHeight();

	    			float scale = 1F; //1.0F is normal size
	    			
	    			
	    			int totalWidth = MathHelper.floor_double((width - 8) / 16);
	    			int totalHeight = (BlockList.getListSize() + BlockList.getListSize() % totalWidth) / totalWidth;
	    			int renderOffset = (width - 8 - totalWidth * 16) / 2;
	    			
	    			GL11.glPushMatrix();
	    			GL11.glEnable(GL11.GL_SCISSOR_TEST);
	    			GL11.glDisable(GL11.GL_TEXTURE_2D);
	    			this.clipToSize(2, 2, width - 4, height - 40, scaling);
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
	    			drawTexturedModalRect(2, 2, 0, 0, width - 4, 4 + totalHeight * 16);
	    			GL11.glEnable(GL11.GL_TEXTURE_2D);
                	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            		GL11.glScalef(scale, scale, scale);
            		RenderHelper.enableGUIStandardItemLighting();
                	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                	this.renderer.zLevel = 200F;
	    			
	    			int row = 0;
	    			int column = 0;
	    			for(int i = 0; i < BlockList.getListSize(); ++i) {
	    				ItemStack item = BlockList.getList().get(i);
	    				if(item == null || item.getItem() == null)
	    					continue;
	    				
	    				if(column >= totalWidth) {
	    					row += 1;
	    					column = 0;
	    				}


	                	if(mouseX > 4 + 16 * column + renderOffset && mouseX < 4 + 16 * (column + 1) + renderOffset && mouseY > 4 + 16 * row && mouseY < 4 + 16 * (row + 1)) {
	                		GL11.glDisable(GL11.GL_TEXTURE_2D);
	                		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	                		drawTexturedModalRect(4 + 16 * column + renderOffset, 4 + 16 * row, 0, 0, 16, 16);
	                		GL11.glEnable(GL11.GL_TEXTURE_2D);

		    				if(Mouse.isButtonDown(1) && hasButtonBeenUp) {
				    			String txtToInsert = String.format((ReflectionHelper.getField(chatField, GuiTextField.class, chat).getText().endsWith(" ") ? "" : " ") +"%s %s ", Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage());
				    			for(int s = 0; s < txtToInsert.length(); s++) {
				    				char ch = txtToInsert.charAt(s);
				    				ReflectionHelper.getField(chatField, GuiTextField.class, chat).textboxKeyTyped(ch, Integer.valueOf(ch));
				    			}
				    			ReflectionHelper.getField(chatField, GuiTextField.class, chat).setCursorPosition(chatPostion + txtToInsert.length());
				    			hasButtonBeenUp = false;
		    				}
	                	}
	                	if(!Mouse.isButtonDown(1)) {
		    				hasButtonBeenUp = true;
		    			}
	                	this.renderer.renderItemIntoGUI(ClientHelper.mc.fontRenderer, ClientHelper.mc.renderEngine, item, 4 + 16 * column + renderOffset, 4 + 16 * row);

	    				column++;
	    			}
	    			
	    			row = 0;
	    			column = 0;
	    			
	            	RenderHelper.disableStandardItemLighting();
	            	GL11.glDisable(GL11.GL_LIGHTING);

	    			GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    			for(int i = 0; i < BlockList.getListSize(); ++i) {
	    				ItemStack item = BlockList.getList().get(i);
	    				if(item == null || item.getItem() == null)
	    					continue;
	    				if(column >= totalWidth) {
	    					row += 1;
	    					column = 0;
	    				}
	    				
	    				if(mouseX > 4 + 16 * column + renderOffset && mouseX < 4 + 16 * (column + 1) + renderOffset && mouseY > 4 + 16 * row && mouseY < 4 + 16 * (row + 1))
	    					drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + item.getDisplayName(), EnumChatFormatting.ITALIC + String.format("%s %s", Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage())), mouseX, mouseY, width, height, ClientHelper.mc.fontRenderer);
	    				column++;
	    			}	
	    			GL11.glPopMatrix();
	            }
	    	}
	    }
	}
	
	public int chatOffset = 0;
	
	@SubscribeEvent
	public void chatEventPre(RenderGameOverlayEvent.Chat event) {
		if(event.type != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
		//isHelperOpen = Mouse.isButtonDown(0);
		if(ClientHelper.mc.currentScreen instanceof GuiChat) {
			ScaledResolution scaling = event.resolution;
			if(this.isHelperOpen) {
				event.posY += 1000;
			}
		}
	}
	
	@SubscribeEvent
	public void chatEventPost(RenderGameOverlayEvent.Post event) {
		if(event.type != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
	}
	
	public void clipToSize(int xPosition, int yPosition, int width, int height, ScaledResolution scaling) {
		int scaleFactor = scaling.getScaleFactor();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(xPosition * scaleFactor, (scaling.getScaledHeight() - (yPosition + height)) * scaleFactor, width * scaleFactor, height * scaleFactor);
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
            RenderHelper.enableGUIStandardItemLighting();
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
	
	 
	 public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
	    {
	        float f = 0.00390625F;
	        float f1 = 0.00390625F;
	        Tessellator tessellator = Tessellator.instance;
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)100, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
	        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)100, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
	        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)100, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
	        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)100, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
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

	        float f3 = (float)(par4 >> 24 & 255) / 255.0F;
	        float f = (float)(par4 >> 16 & 255) / 255.0F;
	        float f1 = (float)(par4 >> 8 & 255) / 255.0F;
	        float f2 = (float)(par4 & 255) / 255.0F;
	        Tessellator tessellator = Tessellator.instance;
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	        GL11.glColor4f(f, f1, f2, f3);
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
