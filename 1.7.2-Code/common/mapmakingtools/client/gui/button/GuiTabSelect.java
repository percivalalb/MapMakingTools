package mapmakingtools.client.gui.button;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public class GuiTabSelect extends GuiButton {
    
	public boolean isSelected;
	public ButtonType type;
	public IFilterClient filter;
	public GuiFilter gui;
	
	public GuiTabSelect(int id, int xPosition, int yPosition, ButtonType type, IFilterClient filter, GuiFilter gui) {
        super(id, xPosition, yPosition, 29, 27, "");
        this.isSelected = false;
        this.type = type;
        this.filter = filter;
        this.gui = gui;
    }
    
    @Override
    public void drawButton(Minecraft mc, int i, int j) {
        if(this.visible) {
        	FontRenderer fontRenderer = mc.fontRenderer;
        	mc.getTextureManager().bindTexture(ResourceReference.tabs);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int difference = isSelected ? 32 : 30;
            this.drawTexturedModalRect(this.xPosition + (isSelected && type == ButtonType.RIGHT ? -2 : 0), this.yPosition, 0 + (isSelected ? 30 : 0), 28 * (type == ButtonType.LEFT ? 1 : 0), difference, 27 * (type == ButtonType.LEFT ? 2 : 1));//top left
            IIcon icon = this.filter.getDisplayIcon();
            mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
            if(icon != null) {
            	this.drawTexturedModelRectFromIcon(this.xPosition + 5 + (type == ButtonType.LEFT ? 3 : 0), this.yPosition + 5, icon, 16, 16);
            }
            if(filter.showErrorIcon(gui)) {
            	this.drawTexturedModelRectFromIcon(this.xPosition + 5 + (type == ButtonType.LEFT ? 3 : 0), this.yPosition + 5, FilterManager.errorIcon, 16, 16);
            }
    	}
	}
    
    public boolean isMouseAbove(int mouseX, int mouseY) {
    	return this.mousePressed(ClientHelper.mc, mouseX, mouseY);
    }
}
