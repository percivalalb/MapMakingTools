package mapmakingtools.client.gui.button;

import net.minecraft.client.Minecraft;

/**
 * @author ProPercivalalb
 */
public class GuiButtonPotentialSpawns extends GuiButtonSmall {

	public GuiButtonPotentialSpawns(int id, int xPosition, int yPosition, int width, int height, String text) {
		super(id, xPosition, yPosition, width, height, text);
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
	    return this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}
}
