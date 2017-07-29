package mapmakingtools.client.gui.button;

/**
 * @author ProPercivalalb
 */
public class GuiSmallButtonData extends GuiSmallButton {

	private int data;
	
	public GuiSmallButtonData(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.data = 0;
	}
	
	public GuiSmallButtonData setData(int data) {
		this.data = data;
		return this;
	}
	
	public int getData() {
		return this.data;
	}
}
