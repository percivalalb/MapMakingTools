package mapmakingtools.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.ModItems;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.datareader.BlockList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 **/
public class ScreenRenderHandler {
	
	private int INDEX_HISTORY = 1;
	private boolean hasButtonBeenUp = true;
	public boolean isHelperOpen = false;
	public RenderItem renderer = ClientHelper.mc.getRenderItem();
	public Field chatField = ReflectionHelper.getField(GuiChat.class, 7);
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		//Event variables
		float partialTicks = event.partialTicks;
	    ScaledResolution resolution = event.resolution;
	    ElementType type = event.type;
		
	    EntityPlayer player = ClientHelper.mc.thePlayer;
	    World world = player.worldObj;
		ItemStack stack = player.getHeldItem();
	    
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / ClientHelper.mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / ClientHelper.mc.displayHeight - 1;
		
		if(type == ElementType.HELMET && stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 0 && PlayerAccess.canEdit(ClientHelper.mc.thePlayer)) {
    		
    		PlayerData data = ClientData.playerData;
    		FontRenderer font = ClientHelper.mc.fontRendererObj;
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

		if(type == ElementType.HELMET && ClientHelper.mc.currentScreen == null && stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 1 && PlayerAccess.canEdit(player)) {
			
			MovingObjectPosition objectMouseOver = ClientHelper.mc.objectMouseOver;
			
			if(objectMouseOver != null) {
	        	List<String> list = new ArrayList<String>();
				
				if(objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
	            	
	            	IBlockState state = world.getBlockState(objectMouseOver.getBlockPos());
	            	ResourceLocation resourceLocation = (ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock());
	            	String id = resourceLocation.toString();
	            	int meta = state.getBlock().getMetaFromState(state);
	            	int stateId = Block.getStateId(state);
	            	
	            	list.add(EnumChatFormatting.YELLOW + "" + id + " | " + meta + " | " + stateId);
	        	    
	            	ModContainer container = Loader.instance().getIndexedModList().get(resourceLocation.getResourceDomain());
	        	    if(container != null)
	        	    	list.add(EnumChatFormatting.ITALIC + "" + container.getName());
	        	    else if(resourceLocation.getResourceDomain().equals("minecraft"))
	        	    	list.add(EnumChatFormatting.ITALIC + "Minecraft");
	        	    else
	        	    	list.add(EnumChatFormatting.ITALIC + "" + EnumChatFormatting.RED + "Unknown");
	        	    
				}
				else if(objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
	           	
		        	Entity entity = objectMouseOver.entityHit;
		        	String id = EntityList.getEntityString(entity);

		            if(id == null && entity instanceof EntityPlayer)
		            	id = "Player";
		            else if(id == null && entity instanceof EntityLightningBolt)
		            	id = "LightningBolt";
		            
		        	list.add(EnumChatFormatting.YELLOW + id + " | " + entity.getEntityId());
		        	
		        	int i = id.indexOf(".");
		        	if(i >= 0) {
		        		String domain = id.substring(0, i);
		        		ModContainer container = Loader.instance().getIndexedModList().get(domain);
		        	    if(container != null)
		        	    	list.add(EnumChatFormatting.ITALIC + "" + container.getName());
		        	    else
		        	    	list.add(EnumChatFormatting.ITALIC + "" + EnumChatFormatting.RED + "Unknown");
		        	}
		        	else
		        		list.add(EnumChatFormatting.ITALIC + "Minecraft");
	           	
				}
				
            	drawHoveringText(list, 0, 25, 1000, 200, ClientHelper.mc.fontRendererObj, false);
			}
		}
		
	    if(Keyboard.isKeyDown(ClientHelper.mc.gameSettings.keyBindSneak.getKeyCode()) && PlayerAccess.canSeeBlockIdHelper(player)) {
	    	if(type == RenderGameOverlayEvent.ElementType.HELMET) {
	   
	    		if(ClientHelper.mc.currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat)ClientHelper.mc.currentScreen;
	    			int chatPostion = ReflectionHelper.getField(chatField, GuiTextField.class, chat).getCursorPosition();
	    			boolean isHovering = false;

	    			float scale = 1F; //1.0F is normal size
	    			
	    			
	    			int totalWidth = MathHelper.floor_double((double)(width - 8) / 16.0D);
	    			FMLLog.info(BlockList.getListSize() + " " + totalWidth);
	    			int totalHeight = MathHelper.floor_double((double)BlockList.getListSize() / (double)totalWidth);
	    			int renderOffset = (width - 8 - totalWidth * 16) / 2;
	    			
	    			GL11.glPushMatrix();
	    			GL11.glEnable(GL11.GL_SCISSOR_TEST);
	    			GL11.glDisable(GL11.GL_TEXTURE_2D);
	    			this.clipToSize(2, 2, width - 4, height - 40, resolution);
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
				    			LogHelper.info(txtToInsert);
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
	                	this.renderer.renderItemIntoGUI(item, 4 + 16 * column + renderOffset, 4 + 16 * row);

	    				column++;
	    			}
	    			
	    			row = 0;
	    			column = 0;

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
	    					drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + item.getDisplayName(), EnumChatFormatting.ITALIC + String.format("%s %s", Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage())), mouseX, mouseY, width, height, ClientHelper.mc.fontRendererObj, true);
	    				column++;
	    			}
	    			
	            	RenderHelper.disableStandardItemLighting();
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
	
	protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)100).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)100).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)100).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)100).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
	
	public void drawHoveringText(List textList, int mouseX, int mouseY, int width, int height, FontRenderer font, boolean titled) {
        if(!textList.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = textList.iterator();

            while(iterator.hasNext()) {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                    k = l;
            }

            int i1 = mouseX + 12;
            int j1 = mouseY - 12;
            int k1 = 8;

            if (textList.size() > 1)
                k1 += 2 + (textList.size() - 1) * 10;
            
            if (i1 + k > width)
                i1 -= 28 + k;

            if (j1 + k1 + 6 > height)
                j1 = height - k1 - 6;

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

            for(int k2 = 0; k2 < textList.size(); ++k2) {
                String s1 = (String)textList.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);

                if(k2 == 0 && titled)
                    j1 += 2;

                j1 += 10;
            }
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(x + 0), (double)(y + height), (double)100).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), (double)100).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + 0), (double)100).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        worldrenderer.pos((double)(x + 0), (double)(y + 0), (double)100).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
	
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		 if (left < right)
	        {
	            int i = left;
	            left = right;
	            right = i;
	        }

	        if (top < bottom)
	        {
	            int j = top;
	            top = bottom;
	            bottom = j;
	        }

	        float f3 = (float)(color >> 24 & 255) / 255.0F;
	        float f = (float)(color >> 16 & 255) / 255.0F;
	        float f1 = (float)(color >> 8 & 255) / 255.0F;
	        float f2 = (float)(color & 255) / 255.0F;
	        Tessellator tessellator = Tessellator.getInstance();
	        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
	        GlStateManager.enableBlend();
	        GlStateManager.disableTexture2D();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.color(f, f1, f2, f3);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
	        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
	        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
	        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
	        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
	        tessellator.draw();
	        GlStateManager.enableTexture2D();
	        GlStateManager.disableBlend();
	}
}
