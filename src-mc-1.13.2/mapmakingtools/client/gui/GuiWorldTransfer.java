package mapmakingtools.client.gui;

import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.inventory.ContainerWorldTransfer;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.tools.worldtransfer.WorldTransferList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class GuiWorldTransfer extends GuiContainer {

	public GuiWorldTransfer() {
		super(new ContainerWorldTransfer());
	}
	
	@Override
    public void initGui() {
    	super.initGui();
    	this.buttonList.clear();
    	this.labelList.clear();
    	
    	int topX = (this.width - 183) / 2;
        int topY = (this.height - 215) / 2;

        int i = 0;
        for(String name : WorldTransferList.getNameList()) {
        	this.buttonList.add(new GuiButtonSmall(i, topX + 135, topY + 50 + i * this.fontRenderer.FONT_HEIGHT, 30, this.fontRenderer.FONT_HEIGHT, "----"));
        	i += 1;
        }
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
	    	
		if (button.enabled) {
	         if(button.id >= 0 && button.id < WorldTransferList.getSize()) {
	        	 TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.worldtransfer.gui.delete", WorldTransferList.getName(button.id));
	     		 chatComponent.getStyle().setItalic(true);
	     		 this.mc.player.sendMessage(chatComponent);
	        	 WorldTransferList.delete(button.id);
	        	 this.mc.setIngameFocus();
	         }
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    	this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int xMouse, int yMouse) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceLib.WORLD_TRANSFER);
        int topX = (this.width - 183) / 2;
        int topY = (this.height - 215) / 2;
        this.drawTexturedModalRect(topX, topY, 0, 0, 183, 215);
        
        GlStateManager.pushMatrix();
		double scale = 1.7D;
		GlStateManager.scale(scale, scale, scale);
		this.fontRenderer.drawStringWithShadow("World Transfer", (int)((topX + 13) / scale), (int)((topY + 13) / scale), -1);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		scale = 0.8D;
		GlStateManager.scale(scale, scale, scale);
		this.fontRenderer.drawString("Cross-world copy and pasting", (int)((topX + 13) / scale), (int)((topY + 28) / scale), 0);
		GlStateManager.popMatrix();
		
		this.fontRenderer.drawStringWithShadow("Name", topX + 13, topY + 38, 4210752);
		
		this.fontRenderer.drawStringWithShadow("Block No", topX + 75, topY + 38, 4210752);
		
		this.fontRenderer.drawStringWithShadow("Delete", topX + 135, topY + 38, 4210752);
		
		int i = 0;
		for(String name : WorldTransferList.getNameList()) {
			this.fontRenderer.drawStringWithShadow(name, topX + 13, topY + 50 + i * this.fontRenderer.FONT_HEIGHT, -1);
			this.fontRenderer.drawStringWithShadow("" + WorldTransferList.getAreaFromName(name).size(), topX + 75, topY + 50 + i * this.fontRenderer.FONT_HEIGHT, -1);
			//GlStateManager.disableTexture2D();
			
			//this.drawRect(topX + 135, topY + 50 + i * this.fontRenderer.FONT_HEIGHT, topX + 135 + 7, topY + 50 + i * this.fontRenderer.FONT_HEIGHT + 7, Color.red.getRGB());
			
			//GlStateManager.enableTexture2D();
			
			i += 1;
		}
		
	}
}
