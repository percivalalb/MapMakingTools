package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

/**
 * @author ProPercivalalb
 */
public class GuiSmallButton extends GuiButton {
    
	public GuiSmallButton(int id, int xPosition, int yPosition, int width, int height, String text) {
        super(id, xPosition, yPosition, width, height, text);
    }
    
    @Override
    public void drawButton(Minecraft minecraft, int i, int j) {
        if(drawButton) {
        	FontRenderer fontRenderer = minecraft.fontRenderer;
        	minecraft.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
            int hoverState = getHoverState(flag);
            this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + hoverState * 20, width / 2, height / 2);//top left
            this.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + hoverState * 20, width / 2, height / 2);//top right
            this.drawTexturedModalRect(xPosition, yPosition + height / 2, 0, 46 + hoverState * 20 + 20 - height / 2, width / 2, height / 2);//bottom left
            this.drawTexturedModalRect(xPosition + width / 2, yPosition + height / 2, 200 - width / 2, 46 + hoverState * 20 + 20 - height / 2, width / 2, height / 2);//bottom right
            this.mouseDragged(minecraft, i, j);
             
            if(!this.enabled) {
                this.drawCenteredString(fontRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffa0a0a0);
            } 
            else {
            	if(flag) {
            		this.drawCenteredString(fontRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffffa0);
            	}
            	else {
            		this.drawCenteredString(fontRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xe0e0e0);
            	}
        	}
    	}
	}
}
