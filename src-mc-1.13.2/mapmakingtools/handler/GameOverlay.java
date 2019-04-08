package mapmakingtools.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lwjgl.opengl.GL11;

import mapmakingtools.ModItems;
import mapmakingtools.client.render.RenderUtil;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.datareader.BlockList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author ProPercivalalb
 **/
public class GameOverlay {
	
	private int INDEX_HISTORY = 1;
	private boolean hasButtonBeenUp = true;
	public boolean isHelperOpen = false;
	public ItemRenderer renderer = ClientHelper.getClient().getItemRenderer();
	public Field chatField = ReflectionHelper.getField(GuiChat.class, 4); // inputField where you enter commands
	private Minecraft mc = Minecraft.getInstance();
	
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		//Event variables
		float partialTicks = event.getPartialTicks();
	    ElementType type = event.getType();
		
	    EntityPlayer player = ClientHelper.getClient().player;
	    World world = player.world;
	    //TODO
		ItemStack stack = player.getHeldItemMainhand();
        int width = mc.mainWindow.getScaledWidth();
        int height = mc.mainWindow.getScaledHeight();
        int mouseX = (int) (mc.mouseHelper.getMouseX() * width / mc.mainWindow.getWidth());
        int mouseY = (int) (height - mc.mouseHelper.getMouseY() * height / mc.mainWindow.getHeight() - 1);
		
		if(type == ElementType.HELMET && stack.getItem() == ModItems.EDIT_ITEM && PlayerAccess.canEdit(ClientHelper.getClient().player)) {
    		
    		PlayerData data = ClientData.playerData;
    		FontRenderer font = ClientHelper.getClient().fontRenderer;
    		GlStateManager.pushMatrix();
    		GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            
            if(data.hasSelectedPoints()) {
        		int[] size = data.getSelectionSize();
            	font.drawStringWithShadow(String.format("Selection Size: %d * %d * %d = %d", size[0], size[1], size[2], data.getBlockCount()), 4, 4, -1);
            }
            else
            	font.drawStringWithShadow("Nothing Selected", 4, 4, -1);
            	
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
    		GlStateManager.popMatrix();
		}

		if(type == ElementType.HELMET && ClientHelper.getClient().currentScreen == null && stack.getItem() == ModItems.WRENCH && PlayerAccess.canEdit(player)) {
			GlStateManager.pushMatrix();
    		GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
			RayTraceResult objectMouseOver = ClientHelper.getClient().objectMouseOver;
			
			if(objectMouseOver != null) {
	        	List<String> list = new ArrayList<String>();
				
				if(objectMouseOver.type == RayTraceResult.Type.BLOCK) {
	            	
	            	IBlockState state = world.getBlockState(objectMouseOver.getBlockPos());
	            	ResourceLocation resourceLocation = (ResourceLocation)ForgeRegistries.BLOCKS.getKey(state.getBlock());
	            	String id = resourceLocation.toString();
	            	int meta = Block.getStateId(state);
	            	
	            	list.add(TextFormatting.YELLOW + "" + id + " | " + meta);
	        	    
	            	Optional<? extends ModContainer> container = ModList.get().getModContainerById(resourceLocation.getNamespace());
	        	    if(container.isPresent())
	        	    	list.add(TextFormatting.ITALIC + "" + container.get().getModInfo().getDisplayName());
	        	    else if(resourceLocation.getNamespace().equals("minecraft"))
	        	    	list.add(TextFormatting.ITALIC + "Minecraft");
	        	    else
	        	    	list.add(TextFormatting.ITALIC + "" + TextFormatting.RED + "Unknown");
	        	    
				}
				else if(objectMouseOver.type == RayTraceResult.Type.ENTITY) {
	           	
		        	Entity entity = objectMouseOver.entity;
		        	ResourceLocation id = EntityType.getId(entity.getType());
		        	
		        	list.add(TextFormatting.YELLOW + id.toString() + " | " + entity.getEntityId());
		
		        	Optional<? extends ModContainer> container = ModList.get().getModContainerById(id.getNamespace());
	        	    if(container.isPresent())
	        	    	list.add(TextFormatting.ITALIC + "" + container.get().getModInfo().getDisplayName());
	        	    else if(id.getNamespace().equals("minecraft"))
	        	    	list.add(TextFormatting.ITALIC + "Minecraft");
	        	    else
	        	    	list.add(TextFormatting.ITALIC + "" + TextFormatting.RED + "Unknown");
		        
	           	
				}
				
            	RenderUtil.drawHoveringText(list, 0, 25, 1000, 200, ClientHelper.getClient().fontRenderer, false);
            	GlStateManager.enableLighting();
            	GlStateManager.enableDepthTest();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableRescaleNormal();
        		GlStateManager.popMatrix();
			}
		}
		
	    if(ClientHelper.getClient().gameSettings.keyBindSneak.isKeyDown() && PlayerAccess.canSeeBlockIdHelper(player)) {
	    	if(type == RenderGameOverlayEvent.ElementType.HELMET) {
	    		this.isHelperOpen = true;
	    		if(ClientHelper.getClient().currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat)ClientHelper.getClient().currentScreen;
	    			int chatPostion = ReflectionHelper.getField(chatField, GuiTextField.class, chat).getCursorPosition();
	    			boolean isHovering = false;

	    			float scale = 1F; //1.0F is normal size
	    			
	    			
	    			int totalWidth = MathHelper.floor((double)(width - 8) / 16.0D);
	    			int totalHeight = MathHelper.ceil((double)BlockList.getListSize() / (double)totalWidth);
	    			int renderOffset = (width - 8 - totalWidth * 16) / 2;
	    			
	    			GlStateManager.pushMatrix();
	    			GL11.glEnable(GL11.GL_SCISSOR_TEST);
	    			GlStateManager.disableTexture2D();
	    			this.clipToSize(2, 2, width - 4, height - 20);
	    			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
	    			RenderUtil.drawTexturedModalRect(2, 2, 0, 0, width - 4, 4 + totalHeight * 16);
	    			GlStateManager.enableTexture2D();
                	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            		GlStateManager.scalef(scale, scale, scale);
            		RenderHelper.enableGUIStandardItemLighting();
                	GlStateManager.enableRescaleNormal();
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
	                		GlStateManager.disableTexture2D();
	                		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	                		RenderUtil.drawTexturedModalRect(4 + 16 * column + renderOffset, 4 + 16 * row, 0, 0, 16, 16);
	                		GlStateManager.enableTexture2D();

		    				if((this.mc.mouseHelper.isRightDown() || this.mc.mouseHelper.isLeftDown()) && hasButtonBeenUp) {
				    			String txtToInsert = String.format((ReflectionHelper.getField(chatField, GuiTextField.class, chat).getText().endsWith(" ") ? "" : " ") +"%s", ForgeRegistries.BLOCKS.getKey(Block.getBlockFromItem(item.getItem())));
				    			for(int s = 0; s < txtToInsert.length(); s++) {
				    				char ch = txtToInsert.charAt(s);
				    				ReflectionHelper.getField(chatField, GuiTextField.class, chat).charTyped(ch, Integer.valueOf(ch));
				    			}
				    			ReflectionHelper.getField(chatField, GuiTextField.class, chat).setCursorPosition(chatPostion + txtToInsert.length());
				    			hasButtonBeenUp = false;
		    				}
	                	}
	                	if(!this.mc.mouseHelper.isRightDown() && !this.mc.mouseHelper.isLeftDown()) {
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
	    					RenderUtil.drawHoveringText(Arrays.asList(TextFormatting.GREEN + item.getDisplayName().getUnformattedComponentText(), TextFormatting.ITALIC + String.format("%s", ForgeRegistries.BLOCKS.getKey(Block.getBlockFromItem(item.getItem())))), mouseX, mouseY, width, height, ClientHelper.getClient().fontRenderer, true);
	    				column++;
	    			}
	    			
	            	RenderHelper.disableStandardItemLighting();
	    			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	    			GlStateManager.popMatrix();
	            }
	    	}
	    }
	    else
	    	this.isHelperOpen = false;
	}
	
	public int chatOffset = 0;
	
	@SubscribeEvent
	public void chatEventPre(RenderGameOverlayEvent.Chat event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
		if(ClientHelper.getClient().currentScreen instanceof GuiChat) {
			if(this.isHelperOpen) {
				event.setPosY(event.getPosY() + 10000);
			}
		}
	}
	
	@SubscribeEvent
	public void chatEventPost(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
	}
	
	public void clipToSize(int xPosition, int yPosition, int width, int height) {
		int scaleFactor = this.mc.mainWindow.getScaleFactor(this.mc.gameSettings.guiScale);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(xPosition * scaleFactor, (this.mc.mainWindow.getScaledHeight() - (yPosition + height)) * scaleFactor, width * scaleFactor, height * scaleFactor);
	}
}
