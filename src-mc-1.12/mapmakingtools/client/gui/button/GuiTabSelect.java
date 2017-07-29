package mapmakingtools.client.gui.button;

import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

/**
 * @author ProPercivalalb
 */
public class GuiTabSelect extends GuiButton {
    
	public boolean isSelected;
	public ButtonType type;
	public FilterClient filter;
	public GuiFilter gui;
	
	public GuiTabSelect(int id, int xPosition, int yPosition, ButtonType type, FilterClient filter, GuiFilter gui) {
        super(id, xPosition, yPosition, 29, 27, "");
        this.isSelected = false;
        this.type = type;
        this.filter = filter;
        this.gui = gui;
    }
    
    @Override
    public void drawButton(Minecraft mc, int i, int j, float partialTicks) {
        if(this.visible) {
        	GL11.glPushMatrix();
        	FontRenderer fontRenderer = mc.fontRenderer;
        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	mc.getTextureManager().bindTexture(ResourceReference.TABS);
        	boolean error = filter.showErrorIcon(gui);
            int difference = isSelected ? 32 : 30;
            this.drawTexturedModalRect(this.x + (isSelected && type == ButtonType.RIGHT ? -2 : 0), this.y, 0 + (isSelected ? 30 : 0), 28 * (type == ButtonType.LEFT ? 1 : 0), difference, 27 * (type == ButtonType.LEFT ? 2 : 1));//top left
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            String path = this.filter.getIconPath();
            mc.getTextureManager().bindTexture(ResourceReference.getTexture(this.filter.getIconPath()));
            this.drawModalRectWithCustomSizedTexture(this.x + 5 + (type == ButtonType.LEFT ? 3 : 0), this.y + 5, 0, 0, 16, 16, 16, 16);
            mc.getTextureManager().bindTexture(ResourceReference.getTexture("mapmakingtools:textures/filter/error.png"));
            if(error) {
            	this.drawModalRectWithCustomSizedTexture(this.x + 5 + (type == ButtonType.LEFT ? 3 : 0), this.y + 5, 0, 0, 16, 16, 16, 16);
            }
            GL11.glPopMatrix();
    	}
	}
    
    public boolean isMouseAbove(int mouseX, int mouseY) {
    	return this.mousePressed(ClientHelper.getClient(), mouseX, mouseY);
    }
}
