package mapmakingtools.client.gui.button;

/**
 * @author ProPercivalalb
 */
public class GuiButtonSmallData extends GuiButtonSmall {

	private int data;
	
	public GuiButtonSmallData(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.data = 0;
	}
	
	public GuiButtonSmallData setData(int data) {
		this.data = data;
		return this;
	}
	
	public int getData() {
		return this.data;
	}
}
