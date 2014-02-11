package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import mapmakingtools.helper.ClientHelper;
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
    public void drawButton(Minecraft mc, int i, int j) {
        if(this.visible) {
        	FontRenderer fontRenderer = mc.fontRenderer;
        	mc.getTextureManager().bindTexture(this.buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = i >= this.xPosition && j >= yPosition && i < this.xPosition + this.width && j < yPosition + this.height;
            int hoverState = getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, yPosition, 0, 46 + hoverState * 20, this.width / 2, this.height / 2);//top left
            this.drawTexturedModalRect(this.xPosition + this.width / 2, yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height / 2);//top right
            this.drawTexturedModalRect(this.xPosition, yPosition + this.height / 2, 0, 46 + hoverState * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom left
            this.drawTexturedModalRect(this.xPosition + this.width / 2, yPosition + this.height / 2, 200 - this.width / 2, 46 + hoverState * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom right
            this.mouseDragged(mc, i, j);
             
            if(!this.enabled) {
                this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, yPosition + (this.height - 8) / 2, 0xffa0a0a0);
            } 
            else {
            	if(this.field_146123_n) {
            		this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, yPosition + (this.height - 8) / 2, 0xffffa0);
            	}
            	else {
            		this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, yPosition + (this.height - 8) / 2, 0xe0e0e0);
            	}
        	}
    	}
	}
    
    public boolean isMouseAbove(int mouseX, int mouseY) {
    	return this.mousePressed(ClientHelper.mc, mouseX, mouseY);
    }
}
