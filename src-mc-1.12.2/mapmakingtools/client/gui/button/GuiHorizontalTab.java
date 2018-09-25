package mapmakingtools.client.gui.button;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author ProPercivalalb
 */
public class GuiHorizontalTab extends GuiButton {
    
	public boolean isSelected;
	public Side side;
	public FilterClient filter;
	public GuiFilter gui;
	
	public GuiHorizontalTab(int id, int xPosition, int yPosition, Side type, FilterClient filter, GuiFilter gui) {
        super(id, xPosition, yPosition, 29, 27, "");
        this.isSelected = false;
        this.side = type;
        this.filter = filter;
        this.gui = gui;
    }
    
    @Override
    public void drawButton(Minecraft mc, int i, int j, float partialTicks) {
        if(this.visible) {
        	GlStateManager.pushMatrix();
        	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        	mc.getTextureManager().bindTexture(ResourceLib.TABS);
        	
        	boolean error = this.filter.showErrorIcon(this.gui);
            boolean left = this.side == Side.LEFT;
            boolean selected = this.isSelected;
            
            this.drawTexturedModalRect(this.x + (selected ? -2 : 0), this.y, error ? (selected ? 94 : 64) : (selected ? 30 : 0), (left ? 28 : 0), selected ? 34 : 30, 28);
            
            if(error)
            	GlStateManager.color(0.7F, 0.25F, 0.25F, 1.0F);
            mc.getTextureManager().bindTexture(ResourceLib.getCachedGeneric(this.filter.getIconPath()));
            Gui.drawModalRectWithCustomSizedTexture(this.x + 5 + (left ? 4 : 0), this.y + 6, 0, 0, 16, 16, 16, 16);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
    	}
	}
    
    public boolean isMouseAbove(int mouseX, int mouseY) {
    	return this.mousePressed(ClientHelper.getClient(), mouseX, mouseY);
    }
    
    public enum Side {

    	LEFT,
    	RIGHT;
    }
}
