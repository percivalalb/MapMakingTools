package mapmakingtools.client.gui.button;

import net.minecraft.client.gui.GuiButton;

/**
 * @author ProPercivalalb
 */
public class GuiButtonData extends GuiButton {

	private int data;
	
	public GuiButtonData(int par1, int par2, int par3, int par4, int par5, String par6Str) {
		super(par1, par2, par3, par4, par5, par6Str);
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
