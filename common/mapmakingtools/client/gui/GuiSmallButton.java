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
    
	public GuiSmallButton(int id, int field_146128_h, int field_146129_i, int field_146120_f, int field_146121_g, String text) {
        super(id, field_146128_h, field_146129_i, field_146120_f, field_146121_g, text);
    }
    
    @Override
    public void func_146112_a(Minecraft mc, int i, int j) {
        if(this.field_146125_m) {
        	FontRenderer fontRenderer = mc.fontRenderer;
        	mc.getTextureManager().bindTexture(this.field_146122_a);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = i >= this.field_146128_h && j >= field_146129_i && i < this.field_146128_h + this.field_146120_f && j < field_146129_i + this.field_146121_g;
            int hoverState = func_146114_a(this.field_146123_n);
            this.drawTexturedModalRect(this.field_146128_h, field_146129_i, 0, 46 + hoverState * 20, this.field_146120_f / 2, this.field_146121_g / 2);//top left
            this.drawTexturedModalRect(this.field_146128_h + this.field_146120_f / 2, field_146129_i, 200 - this.field_146120_f / 2, 46 + hoverState * 20, this.field_146120_f / 2, this.field_146121_g / 2);//top right
            this.drawTexturedModalRect(this.field_146128_h, field_146129_i + this.field_146121_g / 2, 0, 46 + hoverState * 20 + 20 - this.field_146121_g / 2, this.field_146120_f / 2, this.field_146121_g / 2);//bottom left
            this.drawTexturedModalRect(this.field_146128_h + this.field_146120_f / 2, field_146129_i + this.field_146121_g / 2, 200 - this.field_146120_f / 2, 46 + hoverState * 20 + 20 - this.field_146121_g / 2, this.field_146120_f / 2, this.field_146121_g / 2);//bottom right
            this.func_146119_b(mc, i, j);
             
            if(!this.field_146124_l) {
                this.drawCenteredString(fontRenderer, this.field_146126_j, this.field_146128_h + this.field_146120_f / 2, field_146129_i + (this.field_146121_g - 8) / 2, 0xffa0a0a0);
            } 
            else {
            	if(this.field_146123_n) {
            		this.drawCenteredString(fontRenderer, this.field_146126_j, this.field_146128_h + this.field_146120_f / 2, field_146129_i + (this.field_146121_g - 8) / 2, 0xffffa0);
            	}
            	else {
            		this.drawCenteredString(fontRenderer, this.field_146126_j, this.field_146128_h + this.field_146120_f / 2, field_146129_i + (this.field_146121_g - 8) / 2, 0xe0e0e0);
            	}
        	}
    	}
	}
    
    public boolean isMouseAbove(int mouseX, int mouseY) {
    	return this.func_146116_c(ClientHelper.mc, mouseX, mouseY);
    }
}
