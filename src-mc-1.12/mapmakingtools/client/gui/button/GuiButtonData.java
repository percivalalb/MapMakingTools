package mapmakingtools.client.gui.button;

import net.minecraft.client.gui.GuiButton;

/**
 * @author ProPercivalalb
 */
public class GuiButtonData extends GuiButton {

	private int data;
	
	public GuiButtonData(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.data = 0;
	}
	
	public GuiButtonData setData(int data) {
		this.data = data;
		return this;
	}
	
	public int getData() {
		return this.data;
	}
}
