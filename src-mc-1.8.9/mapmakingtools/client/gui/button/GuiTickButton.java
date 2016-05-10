package mapmakingtools.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiTickButton extends GuiButton {
	
	 private static final ResourceLocation beaconGuiTextures = new ResourceLocation("textures/gui/container/beacon.png");
     private boolean ticked;
     
     public GuiTickButton(int id, int xPosition, int yPosition, boolean ticked) {
     	super(id, xPosition, yPosition, 22, 22, "");
     	this.ticked = ticked;
     }

     @Override
     public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    	 if(this.visible) {
         	mc.getTextureManager().bindTexture(beaconGuiTextures);
         	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            short short1 = 219;
            int k = 0;

            if(!this.enabled)
            	k += this.width * 2;
            else if(this.hovered)
            	k += this.width * 3;
                    

            this.drawTexturedModalRect(this.xPosition, this.yPosition, k, short1, this.width, this.height);

            if(this.ticked)
            	this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, 90, 220, 18, 18);
    	 }
     }

     public boolean func_146141_c() {
     	return this.ticked;
     }

     public void func_146140_b(boolean p_146140_1_) {
    	 this.ticked = p_146140_1_;
     }
}
