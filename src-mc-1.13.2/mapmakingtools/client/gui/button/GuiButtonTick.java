package mapmakingtools.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonTick extends GuiButton {
	
	private static final ResourceLocation BEACON_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");
    private boolean ticked;
     
    public GuiButtonTick(int id, int xPosition, int yPosition, boolean ticked) {
    	super(id, xPosition, yPosition, 22, 22, "");
     	this.ticked = ticked;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
    	if(this.visible) {
    		Minecraft minecraft = Minecraft.getInstance();
    		minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
         	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            short short1 = 219;
            int k = 0;

            if(!this.enabled)
            	k += this.width * 2;
            else if(this.hovered)
            	k += this.width * 3;
                    

            this.drawTexturedModalRect(this.x, this.y, k, short1, this.width, this.height);

            if(this.ticked)
            	this.drawTexturedModalRect(this.x + 2, this.y + 2, 90, 220, 18, 18);
    	 }
	}

 	public boolean isTicked() {
 		return this.ticked;
	}

 	public void setTicked(boolean ticked) {
 		this.ticked = ticked;
	}
}
