package mapmakingtools.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public abstract class GuiValueSlider extends GuiButton {
	
    /** The value of this slider control. */
    public float sliderValue = 1.0F;
    public float minValue;
    public float maxValue;

    /** Is this slider control being dragged. */
    public boolean dragging = false;
    
    public GuiValueSlider(int id, int par2, int par3, int width, int height, String par5Str, double value, double minValue, double maxValue) {
        super(id, par2, par3, width, height, par5Str);
        if(value > maxValue) 
        	value = maxValue;
        if(value < minValue)
        	value = minValue;
        this.sliderValue = (float)value / ((float)maxValue - (float)minValue);
        this.minValue = (float)minValue;
        this.maxValue = (float)maxValue;
    }

    @Override
    protected int getHoverState(boolean par1) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
    	if (!Mouse.isButtonDown(0)) 
    		this.dragging = false;
    	if (this.drawButton) {
            if (Mouse.isButtonDown(0) && this.dragging) {
                this.sliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);

                if (this.sliderValue < 0.0F) {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F)
                {
                    this.sliderValue = 1.0F;
                }

                this.onValueChange(this.getValue());
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        if (super.mousePressed(par1Minecraft, par2, par3)) {
            this.sliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);

            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }

            this.onValueChange(this.getValue());
            this.dragging = true;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        this.dragging = false;
    }
    
    public double getValue() {
    	return (double)(this.sliderValue * (maxValue - minValue));
    }
    
    public abstract void onValueChange(double value);
}
