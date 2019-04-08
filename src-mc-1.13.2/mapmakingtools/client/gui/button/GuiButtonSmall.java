package mapmakingtools.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author ProPercivalalb
 */
@OnlyIn(Dist.CLIENT)
public class GuiButtonSmall extends GuiButton {
    
	public GuiButtonSmall(int id, int xPosition, int yPosition, int width, int height, String text) {
        super(id, xPosition, yPosition, width, height, text);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
        	Minecraft minecraft = Minecraft.getInstance();
        	FontRenderer fontrenderer = minecraft.fontRenderer;
        	minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = getHoverState(this.hovered);
            this.drawTexturedModalRect(this.x, y, 0, 46 + hoverState * 20, this.width / 2, this.height / 2);//top left
            this.drawTexturedModalRect(this.x + this.width / 2, y, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height / 2);//top right
            this.drawTexturedModalRect(this.x, y + this.height / 2, 0, 46 + hoverState * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom left
            this.drawTexturedModalRect(this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + hoverState * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom right
            this.renderBg(minecraft, mouseX, mouseY);
             
            if(!this.enabled) {
                this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, y + (this.height - 8) / 2, 0xffa0a0a0);
            } 
            else {
            	if(this.hovered) {
            		this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, y + (this.height - 8) / 2, 0xffffa0);
            	}
            	else {
            		this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, y + (this.height - 8) / 2, 0xe0e0e0);
            	}
        	}
    	}
	}
}
