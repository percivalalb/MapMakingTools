package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

/**
 * @author ProPercivalalb
 */
public class GuiListSlot extends GuiSlot {

	public GuiListSlot(Minecraft minecraft, int width, int height, int top, int bottom, int slotHeight) {
		super(minecraft, width, height, top, bottom, slotHeight);
	}

	@Override
	protected int getSize() {
		return 2;
	}

	@Override
	protected void elementClicked(int i, boolean flag) {
		LogHelper.logDebug("Click");
	}

	@Override
	protected boolean isSelected(int i) {
		return true;
	}

	@Override
	protected void drawBackground() {
		
	}

	@Override
	protected void drawSlot(int i, int j, int k, int l, Tessellator tessellator) {
		LogHelper.logDebug("" + i);
	}
	
	protected void overlayBackground(int par1, int par2, int par3, int par4)
    {
       
    }

    protected void drawContainerBackground(Tessellator tess)
    {
        
    }
}
