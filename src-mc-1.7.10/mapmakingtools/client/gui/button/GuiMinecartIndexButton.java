package mapmakingtools.client.gui.button;

import net.minecraft.client.Minecraft;

/**
 * @author ProPercivalalb
 */
public class GuiMinecartIndexButton extends GuiSmallButton {

	public GuiMinecartIndexButton(int id, int xPosition, int yPosition, int width, int height, String text) {
		super(id, xPosition, yPosition, width, height, text);
	}
	
	@Override
	public boolean mousePressed(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_) {
	    return this.visible && p_146116_2_ >= this.xPosition && p_146116_3_ >= this.yPosition && p_146116_2_ < this.xPosition + this.width && p_146116_3_ < this.yPosition + this.height;
	}

}
