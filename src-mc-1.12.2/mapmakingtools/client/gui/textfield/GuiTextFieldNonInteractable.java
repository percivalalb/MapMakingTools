package mapmakingtools.client.gui.textfield;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author ProPercivalalb
 */
public class GuiTextFieldNonInteractable extends GuiTextField {

	public boolean missMouseClick;
	
	public GuiTextFieldNonInteractable(int id, FontRenderer fontRenderer, int xPos, int yPos, int width, int height) {
		super(id, fontRenderer, xPos, yPos, width, height);
	}

	@Override
	public void setCursorPosition(int index) {
		super.setCursorPosition(this.getMaxStringLength());
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(!this.missMouseClick)
			return super.mouseClicked(mouseX, mouseY, mouseButton);
		else {
			this.missMouseClick = false;
			return false;
		}
	}
}
